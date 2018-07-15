package pt.isec.cub.falldetection._logic.weka;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import pt.isec.cub.falldetection._logic.readings.IClassificationListener;
import pt.isec.cub.falldetection._logic.utils.FileCreator;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.unsupervised.attribute.Remove;

public class MyClassifier {

    private static String TAG = "My Classifier";

    private static ArrayList<String> activities = new ArrayList<String>() {
        {
            add("walking");
            add("running");
            add("sitting");
            add("standing");
            add("jumping");
            add("falling");
            add("others");
        }
    };

    public static MyClassifier instance;

    private FilteredClassifier filteredClassifier;

    public static MyClassifier getInstance(){
        if(instance == null){
            instance = new MyClassifier();
        }

        return instance;
    }

    private MyClassifier(){

    }

    public void loadTrainingData(Context context){

        try {

            String filename = FileCreator.getTrainingArffFilename(context);

            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);

            Instances train = source.getDataSet();

            if(train.classIndex() == -1){
                train.setClassIndex(train.numAttributes() - 1);
            }

            // filter
            Remove rm = new Remove();
            rm.setAttributeIndices("1");  // remove 1st attribute

            // classifier
            J48 j48 = new J48();
            j48.setUnpruned(true);        // using an unpruned J48

            // meta-classifier
            filteredClassifier = new FilteredClassifier();
            filteredClassifier.setFilter(rm);
            filteredClassifier.setClassifier(j48);
            // train and make predictions
            filteredClassifier.buildClassifier(train);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void myClassifier(Context context, IClassificationListener iClassificationListener){

        try {

            if(filteredClassifier == null){
                iClassificationListener.onClassify("No training data!");
                return;
            }

            String filename = FileCreator.createArffTempFile(context);

            if(filename == null){
                iClassificationListener.onClassify("File not found!");
                return;
            }

            Log.d(TAG, "Classifying Data!!!");

            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);

            Instances data = source.getDataSet();

            if(data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }


            for(int i= 0; i<data.numInstances(); i++){
                Double result = filteredClassifier.classifyInstance(data.instance(i));

                iClassificationListener.onClassify(activities.get(result.intValue()));
            }

            FileCreator.deleteArffFileTemp(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void wekaClassifier(Context context, IClassificationListener iClassificationListener){

        try {

            String filename = FileCreator.createArffTempFile(context);

            if(filename == null){
                return;
            }

            Log.d(TAG, "Classifying Data!!!");

            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);

            Instances data = source.getDataSet();

            if(data.classIndex() == -1){
                data.setClassIndex(data.numAttributes() - 1);
            }

            WekaWrapper classifier = new WekaWrapper();

            for(int i= 0; i<data.numInstances(); i++){
                Double result = classifier.classifyInstance(data.instance(i));

                iClassificationListener.onClassify(activities.get(result.intValue()));
            }

            FileCreator.deleteArffFileTemp(context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
