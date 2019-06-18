package com.running.myapplication.Activity;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myapplication.R;
import com.running.myapplication.WordVectorCustom;

import org.bytedeco.javacpp.Loader;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.nativeblas.Nd4jCpu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WebLearningActivity extends AppCompatActivity implements View.OnClickListener {
    List<String> stopwords= new ArrayList<String>(Arrays.asList(" ", ".", ",", "?", "\"", "?", ">",
            "<", "\\", "%",
            "이니","이다","으로","다고","에서","이며","니다","하다","가서","하고","하실","라며","하냐","나면","하기","지는","등등","◆","하겠","됨"
            ,"서는","내려","하여","어져","~","하였","다했","지만","하면","로는","니까","입니","립니","하면","되냐","되는","°","는","를","해서"
            ,"하시","하세","하셔","하신","이나","않냐","어졌","하며","었음","하셨","했","됩","었","하였","해야"
            ,"는","은","을","고","가","인","서","는데","로","된","과","것","들","던","에는","에","다","의","이","▲","★","☆","※","ㄱ","ㄴ","ㄷ","ㅁ"
            ,"\\/",":","^","(",")","-","[","]","/","\\","#","!"+"'","*","+","0","1","2","3","4","5","6","7","8","9","^","ㆍ"));
    EditText edt_address;
    WebView wv_data;
    Button btn_search,btn_setting;
    TextView tv_result;
    SentenceIterator iter;
    Word2Vec webvec = null;
    int layersize=100;
    int windowsize=5;
    int minword=5;
    int itter=1;
    int seed=0;
    int batchsize=512;
    int epoch=1;
    int worker=1;
    double learningrate=0.025;
    String filename="test";

    Collection<String> lst=null;
    String value2=null;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.weblearning_activity);
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
    public void component(){
        tv_result =(TextView)findViewById(R.id.tv_result);
        edt_address = (EditText)findViewById(R.id.edt_address);
        wv_data = (WebView)findViewById(R.id.wv_data);
        WebSettings ws =wv_data.getSettings();
        btn_search=(Button)findViewById(R.id.btn_search);
        btn_setting =(Button)findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        ws.setJavaScriptEnabled(true);
        wv_data.addJavascriptInterface(new JSInterface(),"Android");
        wv_data.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.Android.callback(document.getElementsByTagName('body')[0].innerText);");



            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                String address =edt_address.getText().toString();
                wv_data.loadUrl("http:"+address);
                break;
            case R.id.btn_setting:
                break;
        }
    }
    public class JSInterface {
        @JavascriptInterface
        public void callback(String value) {
            value2=value;
           new LearningTask().execute();
        }
    }
    public void learning(String dd) {
        String word =dd;
       List<String> zz =null;
            zz=   new ArrayList<>();
        zz.add(word);



        iter = new CollectionSentenceIterator(zz);

        TokenizerFactory t = new KoreanTokenizerFactory();
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


        try {
            try {
                webvec = new Word2Vec.Builder()
                        .minWordFrequency(minword) //등장 횟수가 minword 이하인 단어는 무시
                        .iterations(itter)   //문장반복횟수
                        .layerSize(layersize) //output layer size
                        .windowSize(windowsize)  //문장을 몇조각으로 나누는가
                        .iterate(iter)

                        .tokenizerFactory(t)
                        .epochs(epoch) //전체학습반복
                        .batchSize(batchsize)//사전 구축할때 한번에 읽을 단어 수
                        .stopWords(stopwords) //학습할때 무시하는 단어의 리스트
                        .workers(worker)//학습시 사용하는 쓰레드의 갯수 문장이 분할처리되서 학습속도는 빨라지지만 학습결과가 떨어질수도 있음
                        .learningRate(learningrate) //학습설정시 1보다 무조건작게설정해야함
                        .build();

                webvec.fit();
            }catch (Exception e){
                Log.e("에러",e.getMessage().toString());
            }
            final Collection<String> list=webvec.getStopWords();

            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }

                        WordVectorCustom.writeWord2VecModel(webvec,f);

        }catch (IllegalStateException e){
            Toast.makeText(this,"학습오류 해당파일을 확인해주세요",Toast.LENGTH_LONG).show();
        }

    }
Handler h = new Handler();

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
                asyncDialog = ProgressDialog.show(WebLearningActivity.this, null, "학습 중 입니다...", false);

            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    learning(value2);
                }
            });



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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
               //new LearningTask().execute();
            }
        }
    }
}
