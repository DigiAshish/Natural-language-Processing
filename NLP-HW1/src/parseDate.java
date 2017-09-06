import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;

public class parseDate {

	public static void main(String[] args) throws Exception {

		String fileName = "demo1.txt";
		if(args.length > 0 )
		{
			fileName = args[0].toString();	
		}
		String fileAsString = fileToString(fileName);
		matchDate(fileAsString);
	}
	
	static String fileToString(String fileName) throws Exception
	{
		List<String> lineList = Files.readAllLines(new File(fileName).toPath(), Charset.defaultCharset());
		StringBuilder sb = new StringBuilder();
		for(String line : lineList)
		{
			sb.append(line);
		}
		return sb.toString();
	}

	static void matchDate(String fileAsString)
	{
		String date = "([0-2]\\d|3[0-1]|[1-9])";
		String month = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";
		String year = "[12][0-9]{3}";

		String[] regexList = { month+"(\\s)"+date+"(,)*(\\s)*"+year, 
				month+"(\\s)"+date+"(\\s)", 
				month+"(\\s)"+ year, 
				year};
		
		for(String regex : regexList)
		{
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fileAsString);
			while(matcher.find())
				System.out.println(matcher.group());
			System.out.println("**************************");
			matcher.reset();
		}
		
	}

}
