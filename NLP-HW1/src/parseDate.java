import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
//From 
public class parseDate {

	public static void main(String[] args) throws Exception {

		String myFilePath = "demo1.txt";
		if(args.length > 0 )
		{
			myFilePath = args[0].toString();	
		}
		String fileString = fileToString(myFilePath);
		matchDate(fileString);
	}
	

	public static void matchDate(String fileString)
	{
		String date = "([0-3]?[0-9])";
		String month = "(Jan(uary)|Feb(ruary)|Mar(ch)|Apr(il)|May|Jun(e)|Jul(y)|Aug(ust)|Sep(tember)|Oct(ober)|Nov(ember)|Dec(ember))";
		String year = "([0-9]{4})";

		String monthDateYear = month+"(\\s)"+date+"(,)*(\\s)*"+year;
		String monthDate = month+"(\\s)"+date;
		String monthYear = month+"(\\s)"+ year;
		String onlyYear = "(\\s)" + year + "(\\s)";
		
		
		String[] matchStrings = { monthDateYear, monthDate, monthYear, onlyYear};
		for(String strFinal : matchStrings)
		{
			Pattern myPattern = Pattern.compile(strFinal);
			Matcher myMatcher = myPattern.matcher(fileString);
			
			while(myMatcher.find())
				System.out.println(myMatcher.group().toString().trim());
		}
		
	}
	
	static String fileToString(String filePathName) throws Exception
	{
		List<String> lines = Files.readAllLines(Paths.get(filePathName), Charset.defaultCharset());
		StringBuilder sb = new StringBuilder();
		for(String line : lines)
		{
			sb.append(line).append(" ");
		}
		return sb.toString();
	}

}
