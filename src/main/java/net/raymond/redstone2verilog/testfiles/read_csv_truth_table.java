package net.raymond.redstone2verilog.testfiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class read_csv_truth_table {
    public static void main(String[] args) {
        final String ttable_inputs_path = "D:\\onedrive\\University of Warwick\\Wachter, Eduardo - Raymond\\code\\test_files\\inputs_and_gate_truth_table.csv";
        final String ttable_outputs_path = "D:\\onedrive\\University of Warwick\\Wachter, Eduardo - Raymond\\code\\test_files\\outputs_and_gate_truth_table.csv";

        truth_table<Object, Object> myTruthTable = new truth_table<>(ttable_inputs_path, ttable_outputs_path);
        System.out.println(myTruthTable.getInputNames());
        System.out.println(myTruthTable.getOutputNames());
        System.out.println(myTruthTable.getInputData());
        System.out.println(myTruthTable.getOutputData());
        System.out.println(myTruthTable);




    }

}

class truth_table<input, output> {
    private List<String> input_names;
    private List<String> output_names;
    private List<List<String>> input_data;
    private List<List<String>> output_data;

    public truth_table(String input_file_path, String output_file_path) {
        addInputData(input_file_path);
        addOutputData(output_file_path);
    }

    public void addInputData(String input_file_path) {
        List<List<String>> input = processFile(input_file_path);
        this.input_names = input.get(0);
        this.input_data = input.subList(1, input.size());
    }
     public void addOutputData(String output_file_path) {
            List<List<String>> output = processFile(output_file_path);
            this.output_names = output.get(0);
            this.output_data = output.subList(1, output.size());
        }

    public List<List<String>> processFile(String file_path) {
        List<List<String>> file_data = new ArrayList<List<String>>();
        try {
            File file_to_process = new File(file_path);
            Scanner file_reader = new Scanner(file_to_process);

            // add all the data into a variable
            while (file_reader.hasNextLine()) {
                List<String> line = List.of(file_reader.nextLine().split(","));
                file_data.add(line);
            }

            // close file reader
            file_reader.close();

        } catch (FileNotFoundException error) {
            System.out.println("File not found.");
            error.printStackTrace();
        }

        return file_data;
    }

    @Override
    public String toString()
    {
        return "Truth Table\n" + input_names + input_data + "\n" + output_names + output_data;
    }

    public List<String> getInputNames() {
        return input_names;
    }

    public List<String> getOutputNames() {
        return output_names;
    }
    public List<List<String>> getInputData() {
        return input_data;
    }
    public List<List<String>> getOutputData() {
        return output_data;
    }
}