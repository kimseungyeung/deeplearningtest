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
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
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
    Button btn_learning, btn_text;
    EditText edt_word;
    TextView tv_result;
    Word2Vec vec = null;
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
        btn_text.setOnClickListener(this);
        btn_learning.setOnClickListener(this);
        if (vec == null) {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pathToSaveModel.txt");
            try {
                if (f != null && f.exists()) {
                    vec = WordVectorSerializer.loadFullModel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pathToSaveModel.txt");
                }
            } catch (FileNotFoundException e) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_learning:
                learning();
                break;
            case R.id.btn_text:
                if (vec != null) {
                    String word = edt_word.getText().toString().trim();
                    try {
                        lst = vec.wordsNearestSum(word, 10);
                        tv_result.setText("결과: " + lst.toString());
                    } catch (NullPointerException e) {
                        Toast.makeText(this, "결과값이 없습니다.", Toast.LENGTH_LONG).show();
                    }


                }
                break;
        }
    }

    public void learning() {
       lst = null;
        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + "raw/" + "resulttext");
        String path = "";
//                File localFile = new File(Environment.getExternalStorageDirectory(), "raw_sentences.txt");
        File localFile = new File(Environment.getExternalStorageDirectory(), "resulttext.txt");
        iter = new FileSentenceIterator(localFile);

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
//        log.info("Building model....");
        try {
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(2)
                    .iterate(iter)
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
//                    .batchSize(1000)
//                    .build();

            vec.fit();
//                    WordVectorSerializer.writeWord2VecModel(vec, Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"pathToSaveModel.txt");
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pathToSaveModel.txt");
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }
            WordVectorSerializer.writeFullModel(vec, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pathToSaveModel.txt");
        }catch (IllegalStateException e){
            Toast.makeText(this,"학습오류 해당파일을 확인해주세요",Toast.LENGTH_LONG).show();
        }

    }


}
