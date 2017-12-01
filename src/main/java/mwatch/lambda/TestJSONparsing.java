package mwatch.lambda;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJSONparsing {

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		//String jsonInString = "{\"host\":\"122.174.54.189\",\"user\":null,\"method\":\"GET\",\"path\":\"/input\",\"code\":404,\"size\":203,\"referer\":null,\"agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\",\"log_time\":\"2017-03-15T20:24:37Z\"}";
		try {
			// JSON from file to Object
			for(String jsonString:getJsonStrings()){
					HttpLogData obj = mapper.readValue(jsonString, HttpLogData.class);
					System.out.println(obj);
			}
		} catch (Exception e) {
			System.out.println(e);

		}
	}
	
	public static List<String> getJsonStrings(){
		String fileName = "D:\\aws\\httplogs.json";
		List<String> list = new ArrayList<String>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			list = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		list.forEach(System.out::println);
		return list;
	}
	

}
