package com.example.myapplication;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bytedeco.javacv.FrameFilter;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String filePath;
    SentenceIterator iter;
    Button btn_learning;
    EditText edt_word;
    TextView tv_result;
    Word2Vec vec=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        component();

    }
    public void component(){
        tv_result= (TextView)findViewById(R.id.tv_result);
        edt_word =(EditText)findViewById(R.id.edt_word);
        btn_learning=(Button)findViewById(R.id.btn_learning);
        btn_learning.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_learning:
            if(vec==null) {
                Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + "raw/" + "resulttext");
                String path = "";
                File localFile = new File(Environment.getExternalStorageDirectory(), "resulttext.txt");
                iter = new FileSentenceIterator(localFile);

                TokenizerFactory t = new DefaultTokenizerFactory();
                t.setTokenPreProcessor(new CommonPreprocessor());
//        log.info("Building model....");
                Word2Vec vec = new Word2Vec.Builder()
                        .minWordFrequency(3)
                        .iterations(1)
                        .layerSize(8)
                        .seed(3)
                        .windowSize(5)
                        .iterate(iter)
                        .tokenizerFactory(t)
                        .build();

//                Word2Vec vec = new Word2Vec.Builder().minWordFrequency(2).iterations(5).layerSize(100).seed(42)
//                        .learningRate(0.1).windowSize(20).iterate(iter).tokenizerFactory(t).build();
                vec.fit();
            }
                String word =edt_word.getText().toString().trim();
                Collection<String> lst = vec.wordsNearestSum(word, 3);
                if(lst!=null) {
                    tv_result.append(lst.toString());
                }else{
                    Toast.makeText(this,"결과값이 없습니다.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }





}
