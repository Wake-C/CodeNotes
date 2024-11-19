package com.script;


import java.util.regex.Pattern;

public class DramaCategorizer {


    public static void main(String[] args) {
        String directoryPath = "/Volumes/Great Wall/湖南花鼓戏1";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files == null) {
            System.out.println("No files found in the directory.");
            return;
        }

        Pattern pattern = Pattern.compile("《(.*?)》");
        Map<String, List<String>> fileSeriesMap = new HashMap<String, List<String>>();

        for (File file : files) {
            String fileName = file.getName();
            Matcher matcher = pattern.matcher(fileName);
            String seriesName;

            if (matcher.find()) {
                seriesName = matcher.group(1);
            } else {
                // Extract series name without "《》"
                seriesName = fileName.replaceFirst("湖南花鼓戏", "").split(" ")[0];
            }

            if (!fileSeriesMap.containsKey(seriesName)) {
                fileSeriesMap.put(seriesName, new ArrayList<String>());
            }
            fileSeriesMap.get(seriesName).add(fileName);
        }

        // Print the extracted series names and corresponding file names
        for (Map.Entry<String, List<String>> entry : fileSeriesMap.entrySet()) {
            System.out.println("Series: " + entry.getKey());
            for (String fileName : entry.getValue()) {
                System.out.println("  File: " + fileName);
            }
        }
    }
}
