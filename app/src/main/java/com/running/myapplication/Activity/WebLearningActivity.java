package com.running.myapplication.Activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myapplication.R;
import com.running.myapplication.Tokenizer.ExcelTokenizerFactory;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
public class WebLearningActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edt_address;
    WebView wv_data;
    Button btn_search,btn_setting;
    TextView tv_result;
    SentenceIterator iter=null;
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
    List<String> stopword=null;
    Collection<String> lst=null;
    String value2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weblearning_activity);
        component();
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
            // Receive newpid here
            value2=value;
           // tv_result.setText(value);

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

                        .tokenizerFactory(t)
                        .epochs(epoch) //전체학습반복
                        .batchSize(batchsize)//사전 구축할때 한번에 읽을 단어 수
                        .stopWords(stopword) //학습할때 무시하는 단어의 리스트
                        .workers(worker)//학습시 사용하는 쓰레드의 갯수 문장이 분할처리되서 학습속도는 빨라지지만 학습결과가 떨어질수도 있음
                        .learningRate(learningrate) //학습설정시 1보다 무조건작게설정해야함
                        .build();

                vec.fit();
            }catch (Exception e){
                Log.e("에러",e.getMessage().toString());
            }
            final Collection<String> list=vec.getStopWords();

            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/deeplearning/" + "model.data");
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }

                        WordVectorSerializer.writeWord2VecModel(vec,f);

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
                asyncDialog = ProgressDialog.show(WebLearningActivity.this, null, "학습 중 입니다...", false);

            }
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            learning(value2);


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
}
