package com.running.myapplication.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myapplication.Adapter.FileListAdapter;
import com.running.myapplication.R;
import com.running.myapplication.Tokenizer.ExcelTokenizerFactory;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class DeepLearningActivity extends AppCompatActivity implements View.OnClickListener {
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/";
    String filePath;
    SentenceIterator iter;
    DataSetIterator iter2;
    Button btn_learning, btn_text,btn_settting,btn_settingfile;
    EditText edt_word;
    TextView tv_result;
    Word2Vec vec = null;
    int layersize=100;
    int windowsize=5;
    int minword=5;
    int itter=1;
    int seed=0;
    int batchsize=512;
    int epoch=1;
    int worker=3;
    double learningrate=0.025;
    String filename="test";
    List<String>stopword;
        Collection<String> lst=null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.deeplearning_activity);

            component();
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                        //한번권한설정을 거절했을때 나오는부분
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("앱정보->권한\n권한들을 전부 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                                appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(appDetail);
                            }
                        }).setCancelable(false).show();


                    } else {
                        //맨처음 설치시 나오거나 다시보지않기선택시 나오는 부분
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                                1);
                    }
                }else{

                }
            }
    }

    public void component() {
        tv_result = (TextView) findViewById(R.id.tv_result);
        edt_word = (EditText) findViewById(R.id.edt_word);
        btn_learning = (Button) findViewById(R.id.btn_learning);
        btn_text = (Button) findViewById(R.id.btn_text);
        btn_settting=(Button)findViewById(R.id.btn_setting);
        btn_settingfile =(Button)findViewById(R.id.btn_setting_file);
        btn_settting.setOnClickListener(this);
        btn_settingfile.setOnClickListener(this);
        btn_text.setOnClickListener(this);
        btn_learning.setOnClickListener(this);
        SharedPreferences sf = getSharedPreferences("setting_value", MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        layersize = Integer.parseInt(sf.getString("layersize", "100").trim());
        windowsize = Integer.parseInt(sf.getString("windowsize", "5").trim());
        minword =Integer.parseInt(sf.getString("minword", "5").trim());
        itter=Integer.parseInt( sf.getString("iter", "1").trim());
        seed=Integer.parseInt( sf.getString("seed", "0").trim());
        batchsize=Integer.parseInt( sf.getString("batchsize", "512").trim());
        filename=sf.getString("filename", "test").trim();
            epoch=Integer.parseInt( sf.getString("epoch","1"));
            worker=Integer.parseInt( sf.getString("worker","1"));
            learningrate=Double.parseDouble(sf.getString("rate","0.025"));
            String dl4jpath =Environment.getExternalStorageDirectory().getAbsolutePath()+"/deeplearning/"; //폴더 경로
            path = sf.getString("path",dl4jpath);
            File Folder = new File(dl4jpath);

            // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
            if (!Folder.exists()) {
            Folder.mkdir();
        }if(vec==null) {
            new LoadingTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_learning:

                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    ) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                            //한번권한설정을 거절했을때 나오는부분
                            new AlertDialog.Builder(this).setTitle("알림").setMessage("앱정보->권한\n권한들을 전부 허용해주셔야 앱을 이용할 수 있습니다.")
                                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                    appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                                    appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(appDetail);
                                }
                            }).setCancelable(false).show();


                        } else {
                            //맨처음 설치시 나오거나 다시보지않기선택시 나오는 부분
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                                    1);
                        }
                    }else{
                        new LearningTask().execute();
                    }
                }
               else{
                    new LearningTask().execute();
                }
                break;
            case R.id.btn_text:

                if (vec != null) {
                    String word = edt_word.getText().toString();
                    try {



                        Collection<String> ll = vec.wordsNearest(word,10);
                       boolean hasword=vec.hasWord(word);
                       String check="없음";
                       if(hasword){
                           check="있음";
                       }
                       INDArray dd =vec.getWordVectorMatrix(word);
                        tv_result.setText("\n검색유무:"+ check);
                        lst = vec.wordsNearestSum(word, 10);
                        tv_result.append("결과: " + lst.toString());
                        tv_result.append("\n결과2: "+ll.toString());

//                        tv_result.setText("\n결과2: "+ll.toString());

                    } catch (NullPointerException e) {
                        Toast.makeText(this, "결과값이 없습니다.", Toast.LENGTH_LONG).show();
                    }


                }
                break;
            case R.id.btn_setting:
                setDialog();
                break;
            case R.id.btn_setting_file:
                setfileDialog();
                break;
        }
    }

    public void learning() {
       lst = null;
        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + "raw/" + "resulttext");
       // String path = "";
//                File localFile = new File(Environment.getExternalStorageDirectory(), "raw_sentences.txt");

        File localFile = new File(path, filename);

       // iter = new FileSentenceIterator(localFile);
        try {
//            RecordReader recordReader = new CSVRecordReader(0,",");
//
//            recordReader.initialize(new FileSplit(localFile));
//            iter2 = new RecordReaderDataSetIterator(recordReader, 512, 6, 0);

//            MultiLayerNetwork model = new MultiLayerNetwork(conf);
//            FileSentenceIterator d =new RecordReaderDataSetIterator(recordReader, 512, 6, 0);
        }catch (Exception e){

        }
      /*  try {*/

          //  iter = new BasicLineIterator(localFile.getAbsolutePath());
            iter = new LineSentenceIterator(localFile);
      /*  }catch (FileNotFoundException e){
            Log.e("이더실패",e.getMessage().toString());
        }*/
       // TokenizerFactory t = new DefaultTokenizerFactory();
//        TokenizerFactory  t= new KoreanTokenizerFactory();
        TokenizerFactory t = new ExcelTokenizerFactory(0);
        t.setTokenPreProcessor(new CommonPreprocessor());
       iter.setPreProcessor(new SentencePreProcessor() {
           @Override
           public String preProcess(String sentence) {
               String s= sentence;
               s=s.replace("“","");
               s=s.replace("”","");
               return s;
           }
       });
//        log.info("Building model....");
        stopword=new ArrayList<>();
        stopword.add(" ");
        stopword.add(".");
        stopword.add(",");
        stopword.add("?");
        stopword.add("\"");
        stopword.add("?");
        stopword.add(">");
        stopword.add("<");
        stopword.add("\\");
        stopword.add("%");
        stopword.add("＂");
        stopword.add("”");
        stopword.add("˝");
        stopword.add("″");
        stopword.add("”");
        stopword.add("“");
        stopword.add("”");

        try {
           try {
               vec = new Word2Vec.Builder()
                       .minWordFrequency(minword) //등장 횟수가 minword 이하인 단어는 무시
                       .iterations(itter)   //문장반복횟수
                       .layerSize(layersize) //output layer size
                       .windowSize(windowsize)  //문장을 몇조각으로 나누는가
                       .iterate(iter)
//                    .limitVocabularySize(1)  //사용하는 어휘수의 제한
//                    .useHierarchicSoftmax(true)
//                    .useAdaGrad(true)
//                    .seed(seed) //랜덤난수 적용
                       .tokenizerFactory(t)
                       .epochs(epoch) //전체학습반복
                       .batchSize(batchsize)//사전 구축할때 한번에 읽을 단어 수
                       .stopWords(stopword) //학습할때 무시하는 단어의 리스트
                       .workers(worker)//학습시 사용하는 쓰레드의 갯수 문장이 분할처리되서 학습속도는 빨라지지만 학습결과가 떨어질수도 있음
                       .learningRate(learningrate) //학습설정시 1보다 무조건작게설정해야함


//                    .useAdaGrad(true)
//                       .useHierarchicSoftmax(true)

                       .build();

               vec.fit();
           }catch (Exception e){
            Log.e("에러",e.getMessage().toString());
           }
            final Collection<String>list=vec.getStopWords();


//                    WordVectorSerializer.writeWord2VecModel(vec, Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"pathToSaveModel.txt");
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }

//            WordVectorSerializer.writeFullModel(vec, Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "pathToSaveModel.txt");
            WordVectorSerializer.writeWord2VecModel(vec,Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");
            //            WordVectorSerializer.writeWord2VecModel(vec,f);
            settexthandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_result.setText(list.toString());
                }
            });
        }catch (IllegalStateException e){
            Toast.makeText(this,"학습오류 해당파일을 확인해주세요",Toast.LENGTH_LONG).show();
        }

    }

    public class LearningTask extends AsyncTask<Integer, Integer, Boolean> {
        ProgressDialog asyncDialog = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isFinishing() && this != null) {
//                asyncDialog = new ProgressDialog(DeepLearningActivity.this);
//                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                asyncDialog.setMessage("학습 중 입니다...");
//                asyncDialog.setCancelable(false);
//                asyncDialog.show();
                asyncDialog = ProgressDialog.show(DeepLearningActivity.this, null, "학습 중 입니다...", false);

            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            learning();


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (asyncDialog.isShowing()) {
                asyncDialog.dismiss();
            }
        }
    }
    public class LoadingTask extends AsyncTask<Integer, Integer, Boolean> {
        ProgressDialog asyncDialog = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isFinishing() && this != null) {
                asyncDialog = new ProgressDialog(DeepLearningActivity.this);
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("로딩 중 입니다...");
                asyncDialog.setCancelable(false);
                asyncDialog.show();

            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            if (vec == null) {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");
                try {
                    if (f != null && f.exists()) {
//                        vec = WordVectorSerializer.loadFullModel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "pathToSaveModel.txt");
                        vec=WordVectorSerializer.readWord2VecModel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");

                    }
                } catch (Exception e) {
                    Log.e("로드실패",e.getMessage().toString());
            }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (asyncDialog.isShowing()) {
                asyncDialog.dismiss();
            }
        }
    }
    public void setDialog() {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.setting_dialog, null);
        dialogBuilder.setView(view);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText edt_layersize= (EditText)view.findViewById(R.id.edt_layersize);
        final EditText edt_windowsize= (EditText)view.findViewById(R.id.edt_windowsize);
        final EditText edt_minword= (EditText)view.findViewById(R.id.edt_minword);
        final EditText edt_iter =(EditText)view.findViewById(R.id.edt_iter);
        final EditText edt_seed =(EditText)view.findViewById(R.id.edt_seed);
        final EditText edt_batchsize =(EditText)view.findViewById(R.id.edt_batch);
        final EditText edt_epoch =(EditText)view.findViewById(R.id.edt_epoch);
        final EditText edt_worker =(EditText)view.findViewById(R.id.edt_worker);
        final EditText edt_rate =(EditText)view.findViewById(R.id.edt_rate);
        Button btn_set=(Button)view.findViewById(R.id.btn_set);
        Button btn_cancel=(Button)view.findViewById(R.id.btn_cancle);
        edt_layersize.setText(String.valueOf(layersize));
        edt_windowsize.setText(String.valueOf(windowsize));
        edt_minword.setText(String.valueOf(minword));
        edt_iter.setText(String.valueOf(itter));
        edt_seed.setText(String.valueOf(seed));
        edt_batchsize.setText(String.valueOf(batchsize));
        edt_epoch.setText(String.valueOf(epoch));
        edt_worker.setText(String.valueOf(worker));
        edt_rate.setText(String.valueOf(learningrate));
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String slayersize=edt_layersize.getText().toString().trim();
                String swindowsize=edt_windowsize.getText().toString().trim();
                String sminword=edt_minword.getText().toString().trim();
                String siter=edt_iter.getText().toString().trim();
                String sseed=edt_seed.getText().toString().trim();
                String sbatch=edt_batchsize.getText().toString().trim();
                String sepoch=edt_epoch.getText().toString().trim();
                String sworker=edt_worker.getText().toString().trim();
                String srate=edt_rate.getText().toString().trim();
                if(!slayersize.equals("")) {
                    layersize = Integer.parseInt(slayersize);
                }
                if(!swindowsize.equals("")) {
                    windowsize = Integer.parseInt(swindowsize);
                }
                if(!sminword.equals("")) {
                    minword = Integer.parseInt(sminword);
                }
                if(!siter.equals("")) {
                    itter = Integer.parseInt(siter);
                }
                if(!sseed.equals("")) {
                    seed = Integer.parseInt(sseed);
                }
                if(!sbatch.equals("")) {
                   batchsize = Integer.parseInt(sbatch);
                }
                if(!sepoch.equals("")) {
                    epoch = Integer.parseInt(sepoch);
                }
                if(!sworker.equals("")) {
                    worker = Integer.parseInt(sworker);
                }
                if(!srate.equals("")) {
                    learningrate = Double.parseDouble(srate);
                }
                save_setting(slayersize,swindowsize,siter,sminword,sseed,sbatch,sepoch,sworker,srate);
                alertDialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
    public void setfileDialog() {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.filelist_dialog, null);
        dialogBuilder.setView(view);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RecyclerView flist = (RecyclerView) view.findViewById(R.id.flist);
        Button btn_set = (Button) view.findViewById(R.id.btn_set);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
//        edt_filename.setText(filename);

        File dirFile = new File(path);
        File[] fileList = dirFile.listFiles();
        ArrayList<File> filelist= new ArrayList<>();
        File f= new File ("...");
        filelist.add(f);
        int selectidx=-1;
        for (File tempFile : fileList) {

                filelist.add(tempFile);
                if(filename.equals(tempFile.getName())){
                    selectidx=filelist.size()-1;
                }

        }
            final FileListAdapter flistadapter= new FileListAdapter(filelist,this);
           flistadapter.setItemClick(new FileListAdapter.ItemClick() {
               @Override
               public void onClick(View view, int position, File fdata) {
                   if(!fdata.getName().equals("...")&&fdata.isFile()) {
                       path =fdata.getParentFile().getAbsolutePath();
                       flistadapter.clickidx(position);
                   }else{
                       flistadapter.clickidx(position);
                       File dirFile = new File(path);
                       if(fdata.getName().equals("...")) {
                           path = dirFile.getParentFile().getAbsolutePath();
                       }else{
                           path = fdata.getAbsolutePath();
                       }
                       dirFile = new File(path);
                       File[] fileList = dirFile.listFiles();
                       fileList = sortFileList(fileList,0);
                       ArrayList<File> filelist= new ArrayList<>();
                       File dir = new File(path);
                       if(!dir.getPath().equals( Environment.getExternalStorageDirectory().getAbsolutePath())) {
                           File f = new File("...");
                           filelist.add(f);
                       }
                       int selectidx=-1;
                       for (File tempFile : fileList) {
                           if(tempFile.isDirectory()) {
                               filelist.add(tempFile);
                               if (filename.equals(tempFile.getName())) {
                                   selectidx = filelist.size() - 1;
                               }
                           }
                       }
                       for (File tempFile : fileList) {
                            if(tempFile.isFile()) {
                                filelist.add(tempFile);
                                if (filename.equals(tempFile.getName())) {
                                    selectidx = filelist.size() - 1;
                                }
                            }
                       }
                       flistadapter.setFilelist(filelist);
                   }
               }
           });
            flist.setAdapter(flistadapter);
            flistadapter.clickidx(selectidx);
            flist.setLayoutManager(new LinearLayoutManager(this));
            btn_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sfilename=flistadapter.getfilename();

                    if (!sfilename.equals("")) {
                        filename = sfilename;
                    }

                    save_setting2(filename,path);
                    alertDialog.cancel();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
            alertDialog.show();

    }
    Handler settexthandler = new Handler();


    public void save_setting(String layersize, String windowsize,String iter,String minword,String seed,String batch,String epoc,String worke,String rate) {
        SharedPreferences sharedPreferences = getSharedPreferences("setting_value", MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String text = edt_email.getText().toString(); // 사용자가 입력한 저장할 데이터
        editor.putString("layersize", layersize); // key, value를 이용하여 저장하는 형태
        editor.putString("windowsize", windowsize);
        editor.putString("iter", iter);
        editor.putString("minword",minword);
        editor.putString("seed",seed);
        editor.putString("batchsize",batch);
        editor.putString("epoch",epoc);
        editor.putString("worker",worke);
        editor.putString("rate",rate);
        editor.commit();
    }
    public void save_setting2(String fname,String fpath){
        SharedPreferences sharedPreferences = getSharedPreferences("setting_value", MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("filename",fname);
        editor.putString("path",fpath);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        Toast.makeText(this, "권한오류", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
            &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                new LearningTask().execute();
            }
        }
    }
    public File[] sortFileList(File[] files, final int compareType)
    {

        Arrays.sort(files,
                new Comparator<Object>()
                {
                    @Override
                    public int compare(Object object1, Object object2) {

                        String s1 = "";
                        String s2 = "";

                        if(compareType == 0){
                            s1 = ((File)object1).getName();
                            s2 = ((File)object2).getName();
                        }
                        else if(compareType == 1){
                            s1 = ((File)object1).lastModified()+"";
                            s2 = ((File)object2).lastModified()+"";
                        }


                        return s1.compareTo(s2);

                    }
                });

        return files;
    }


}
