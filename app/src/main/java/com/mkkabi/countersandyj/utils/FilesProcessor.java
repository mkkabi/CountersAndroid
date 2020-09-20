package com.mkkabi.countersandyj.utils;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FilesProcessor {

    public static String readFileAsString(String fileName){
        String result = null;
        StringBuilder builder = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            reader.lines().forEach((line)->builder.append(line));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static List<List<String>> readFileAsList(String fileName){
        List<List<String >> result = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            result = reader.lines().map((line)->{
                return Arrays.stream(line.split(",")).map(String::trim).collect(Collectors.toList());
            }).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
