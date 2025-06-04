package org.example.agents;

import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;

import java.io.*;
import java.util.*;

public class ImageObjectExtractor {

    private static final String MODEL_CONFIGURATION = "yolov4.cfg";
    private static final String MODEL_WEIGHTS = "yolov4.weights";
    private static final String COCO_NAMES = "coco.names";
    private static final float CONFIDENCE_THRESHOLD = 0.5f;
    private static final float NMS_THRESHOLD = 0.4f;

    private List<String> classNames;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public ImageObjectExtractor() throws IOException {
        classNames = loadClassNames(COCO_NAMES);
    }

    private List<String> loadClassNames(String filename) throws IOException {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
        }
        return names;
    }

    public List<String> extractLabels(File imageFile) throws IOException {
        List<String> detectedLabels = new ArrayList<>();

        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        if (image.empty()) {
            throw new IOException("Failed to load image " + imageFile.getAbsolutePath());
        }

        Net net = Dnn.readNetFromDarknet(MODEL_CONFIGURATION, MODEL_WEIGHTS);

        Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(416, 416), new Scalar(0, 0, 0), true, false);
        net.setInput(blob);

        List<String> outNames = net.getUnconnectedOutLayersNames();

        List<Mat> outputs = new ArrayList<>();
        net.forward(outputs, outNames);

        List<Integer> classIds = new ArrayList<>();
        List<Float> confidences = new ArrayList<>();
        List<Rect2d> boxes = new ArrayList<>();

        int width = image.width();
        int height = image.height();

        for (Mat result : outputs) {
            for (int i = 0; i < result.rows(); i++) {
                Mat row = result.row(i);
                Mat scores = row.colRange(5, result.cols());
                Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                float confidence = (float) mm.maxVal;
                int classId = (int) mm.maxLoc.x;

                if (confidence > CONFIDENCE_THRESHOLD) {
                    float centerX = (float) (row.get(0, 0)[0] * width);
                    float centerY = (float) (row.get(0, 1)[0] * height);
                    float boxWidth = (float) (row.get(0, 2)[0] * width);
                    float boxHeight = (float) (row.get(0, 3)[0] * height);

                    int left = (int) (centerX - boxWidth / 2);
                    int top = (int) (centerY - boxHeight / 2);

                    classIds.add(classId);
                    confidences.add(confidence);
                    boxes.add(new Rect2d(left, top, (int) boxWidth, (int) boxHeight));
                }
            }
        }

        MatOfFloat confidencesMat = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
        MatOfRect2d boxesMat = new MatOfRect2d();
        boxesMat.fromList(boxes);
        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(boxesMat, confidencesMat, CONFIDENCE_THRESHOLD, NMS_THRESHOLD, indices);

        for (int idx : indices.toArray()) {
            int classId = classIds.get(idx);
            if (classId >= 0 && classId < classNames.size()) {
                detectedLabels.add(classNames.get(classId));
            }
        }

        return detectedLabels;
    }
}