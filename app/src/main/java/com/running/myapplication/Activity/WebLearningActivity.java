package com.running.myapplication.Activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
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
    SentenceIterator iter=null;
    Word2Vec vec = null;
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
                setDialog();
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

     /*   stopword=new ArrayList<>();
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
        stopword.add("-");
        stopword.add("을");
        stopword.add("은");
        stopword.add("이");
        stopword.add("는");
        stopword.add("의");
        stopword.add("다");
        stopword.add("에");
        stopword.add("(");
        stopword.add(")");
        stopword.add("를");
        stopword.add("고");
        stopword.add("로");
        stopword.add("이다");
        stopword.add("으로");
        stopword.add("에서");
        stopword.add("것");
        stopword.add("된");*/

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
                        .stopWords(stopwords) //학습할때 무시하는 단어의 리스트
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
 /*   for(int i=0; i<10; i++) {
       tv_result.append(vec.getVocab().elementAtIndex(i).getWord()+" ");
    }*/
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
            tv_result.setText("");
            for(int i=0; i<10; i++) {
              String word=  vec.getVocab().elementAtIndex(i).getWord();
              tv_result.append(word+",");
            }
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
}
