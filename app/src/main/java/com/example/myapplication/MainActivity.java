package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bytedeco.javacv.FrameFilter;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String filePath;
    SentenceIterator iter;
    Button btn_learning, btn_text,btn_settting,btn_settingfile;
    EditText edt_word;
    TextView tv_result;
    Word2Vec vec = null;
    int layersize=100;
    int windowsize=5;
    int minword=5;
    int itter=1;
    String filename="test";
        Collection<String> lst=null;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            component();

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
        filename=sf.getString("filename", "test").trim();
        String path =Environment.getExternalStorageDirectory().getAbsolutePath()+"/deeplearning/"; //폴더 경로
        File Folder = new File(path);

        // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
        if (!Folder.exists()) {
            Folder.mkdir();
        }
        new LoadingTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_learning:
                new LearningTask().execute();
                break;
            case R.id.btn_text:

                if (vec != null) {
                    String word = edt_word.getText().toString();
                    try {
                        lst = vec.wordsNearestSum(word, 10);
                    Collection<String> ll = vec.wordsNearest(word,10);
                        tv_result.setText("결과: " + lst.toString());
                        tv_result.append("\n결과2: "+ll.toString());
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
        String path = "";
//                File localFile = new File(Environment.getExternalStorageDirectory(), "raw_sentences.txt");

        File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/deeplearning/", filename+".txt");
        iter = new FileSentenceIterator(localFile);
        TokenizerFactory t = new DefaultTokenizerFactory();

        t.setTokenPreProcessor(new CommonPreprocessor());
//        log.info("Building model....");
        try {
            vec = new Word2Vec.Builder()
                    .minWordFrequency(minword) //등장 횟수가 minword 이하인 단어는 무시
                    .iterations(itter)   //학습반복횟수
                    .layerSize(layersize)
                    .windowSize(windowsize)
                    .iterate(iter)
                    .seed(42)
                    .tokenizerFactory(t)
                    .build();
//            vec = new Word2Vec.Builder()
//                    .minWordFrequency(5)
//                    .iterations(1)
//                    .layerSize(100)
//                    .seed(42)
//                    .windowSize(5)
//                    .iterate(iter)
//                    .tokenizerFactory(t)
//                    .batchSize(1000) // 사전을 구축할때 한번에 읽을 단어 수
//                    .build();

            vec.fit();
//                    WordVectorSerializer.writeWord2VecModel(vec, Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"pathToSaveModel.txt");
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "pathToSaveModel.txt");
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }
            WordVectorSerializer.writeFullModel(vec, Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "pathToSaveModel.txt");
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
                asyncDialog = new ProgressDialog(MainActivity.this);
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("학습 중 입니다...");
                asyncDialog.setCancelable(false);
                asyncDialog.show();

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
                asyncDialog = new ProgressDialog(MainActivity.this);
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("로딩 중 입니다...");
                asyncDialog.setCancelable(false);
                asyncDialog.show();

            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            if (vec == null) {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pathToSaveModel.txt");
                try {
                    if (f != null && f.exists()) {
                        vec = WordVectorSerializer.loadFullModel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "pathToSaveModel.txt");
                    }
                } catch (FileNotFoundException e) {

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
        Button btn_set=(Button)view.findViewById(R.id.btn_set);
        Button btn_cancel=(Button)view.findViewById(R.id.btn_cancle);
        edt_layersize.setText(String.valueOf(layersize));
        edt_windowsize.setText(String.valueOf(windowsize));
        edt_minword.setText(String.valueOf(minword));
        edt_iter.setText(String.valueOf(itter));
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String slayersize=edt_layersize.getText().toString().trim();
                String swindowsize=edt_windowsize.getText().toString().trim();
                String sminword=edt_minword.getText().toString().trim();
                String siter=edt_iter.getText().toString().trim();
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
                save_setting(slayersize,swindowsize,siter,sminword);
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
        View view = inflater.inflate(R.layout.setting_file_dialog, null);
        dialogBuilder.setView(view);

        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText edt_filename= (EditText)view.findViewById(R.id.edt_fielname);
        Button btn_set=(Button)view.findViewById(R.id.btn_set);
        Button btn_cancel=(Button)view.findViewById(R.id.btn_cancle);
        edt_filename.setText(filename);

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sfilename=edt_filename.getText().toString().trim();

                if(!sfilename.equals("")) {
                    filename = sfilename;
                }

              save_setting2(filename);
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
    public void save_setting(String layersize, String windowsize,String iter,String minword) {
        SharedPreferences sharedPreferences = getSharedPreferences("setting_value", MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String text = edt_email.getText().toString(); // 사용자가 입력한 저장할 데이터
        editor.putString("layersize", layersize); // key, value를 이용하여 저장하는 형태
        editor.putString("windowsize", windowsize);
        editor.putString("iter", iter);
        editor.putString("minword",minword);
        editor.commit();
    }
    public void save_setting2(String fname){
        SharedPreferences sharedPreferences = getSharedPreferences("setting_value", MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("filename",fname);
        editor.commit();
    }
}
