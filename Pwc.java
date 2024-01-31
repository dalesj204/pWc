import java.io.*;
import java.util.*;


public class Pwc {

    public static void main(String[] args) throws FileNotFoundException {
        run(args);
        
    }

    // runs pwc method
    public static void run(String[] args) throws FileNotFoundException {
        //check if the commandline contains file and sees if file exists
        if(args[0] != null){
            for(int i = 0; i < args.length; i++){
                if(args[i].startsWith("-")){ // if command line starts with - or --
                    if(!args[i].equals("-v") && !args[i].equals("-h") && 
                    !args[i].equals("--version") && !args[i].equals("--help")){
                        i++;
                        File tempFile = new File(args[i].trim());
                        if(!tempFile.exists()){
                        System.out.println("Invalid file(s). Try again.");
                        return;
                        }
                    }
                    
                } else {
                    File tempFile = new File(args[i].trim());
                    if(!tempFile.exists()){
                    System.out.println("Invalid file(s). Try again.");
                    return;
                    }
                }
                //System.out.println("args[i] = " + args[i]);
                
            }
        }
        
        int indexToStart = 1; // for later use
        int totalWords = 0;
        int totalLines = 0;
        int totalBytes = 0;
        int numberOfFiles = 0;
        boolean words = false;
        boolean lines = false;
        boolean bytes = false;
        if(args[0].startsWith("--")){ // long flag option
            //System.out.println("long flag");
            numberOfFiles = fileCount(args, indexToStart);
            //System.out.println("num of files = " + numberOfFiles);
            String longArg = args[0].substring(2);
            if(longArg.equals("version")){ // version option
                System.out.println(System.getProperty("java.version"));
                return;
            }
            if(longArg.equals("help")){ // help option
                helpMessage();
                return;
            } 
            for(int i = indexToStart; i < args.length; i++){ // go through for each file
                File file = new File(args[i]);
                if(longArg.equals("words")){ // word option
                    if(file.exists()){
                        System.out.print(wordCount(file) + "    ");
                        System.out.println(file);
                        totalWords += wordCount(file);
                        words = true;
                    }
                }
                if(longArg.equals("lines")){ // line option
                    if(file.exists()){
                        System.out.print(lineCount(file) + "    ");
                        System.out.println(file);
                        totalLines += lineCount(file);
                        lines = true;
                    }
                }
                if(longArg.equals("bytes")){ // byte option
                    if(file.exists()){
                        System.out.print(byteCount(file) + "    ");
                        System.out.println(file);
                        totalBytes += byteCount(file);
                        bytes = true;
                    }
                }
                if(!longArg.equals("words") && !longArg.equals("lines") && !longArg.equals("bytes")) {
                    System.out.println("Long flag could not be identified.");
                    return;
                }
            }
            
        } else if(args[0].startsWith("-")){ // short flag
            //System.out.println("short flag");
            String shortArg = args[0].substring(1);
            // System.out.println("short arg is " + shortArg);
            numberOfFiles = fileCount(args, indexToStart);
            //System.out.println("num of files = " + numberOfFiles);
            if(shortArg.equals("v")){ // version option
                System.out.println(System.getProperty("java.version"));
                return;
            } else if(shortArg.equals("h")){ // help option
                helpMessage();
                return;
            } 
            for(int i = indexToStart; i < args.length; i++){ // go through for each file
                File file = new File(args[i]);
                if(shortArg.equals("w")){ // word option
                    if(file.exists()){
                        System.out.print(wordCount(file) + "    ");
                        System.out.println(file);
                        totalWords += wordCount(file);
                        words = true;
                    }
                }
                if(shortArg.equals("l")){ // line option
                    if(file.exists()){
                        System.out.print(lineCount(file) + "    ");
                        System.out.println(file);
                        totalLines += lineCount(file);
                        lines = true;
                    }
                }
                if(shortArg.equals("c")){ // byte option
                    if(file.exists()){
                        System.out.print(byteCount(file) + "    ");
                        System.out.println(file);
                        totalBytes += byteCount(file);
                        bytes = true;
                    }
                } 
                if(!shortArg.equals("w") && !shortArg.equals("l") && !shortArg.equals("c")) {
                    System.out.println("Short flag could not be identified.");
                    return;
                }
            }
            
        } else { // if only file name(s) are entered or invalid file names/ input
            //System.out.println("no flags");
            indexToStart = 0;
            words = true;
            lines = true;
            bytes = true;
            for(int i = 0; i < args.length; i++){ 
                File file1 = new File(args[i]);
                System.out.print(wordCount(file1) + "    ");
                totalWords += wordCount(file1);
                System.out.print(lineCount(file1) + "    ");
                totalLines += lineCount(file1);
                System.out.print(byteCount(file1) + "    ");
                totalBytes += byteCount(file1);
                System.out.println(file1);
            }
        }
        if(numberOfFiles > 1){ // at very end, print the totals
            if(words){
                System.out.print(totalWords + "   ");
            }
            if(lines){
                System.out.print(totalLines + "   ");
            }
            if(bytes){
                System.out.print(totalBytes + "   ");
            }
            System.out.println("total");
        }
    } // run method

    public static int fileCount(String[] args, int startingIndex){
        return args.length - startingIndex; // gives the number of files
    }

    public static void helpMessage(){ 
        System.out.println("Options:");
        System.out.println("    -v, --version   Show version and exit");
        System.out.println("    -h, --help      Display this message and exit");
        System.out.println("    -w, --words     Show word count");
        System.out.println("    -l, --lines     Show line count");
        System.out.println("    -c, --bytes     Show byte count");
        System.out.println("    [FILENAME]...   Show word, line, and byte count");       
    }

    public static int wordCount(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        int count = 0;
        while(input.hasNextLine()){
            String line = input.nextLine();
            Scanner lineScan = new Scanner(line);
            while(lineScan.hasNext()){
                count++;
                lineScan.next();
            }
        }
        return count;
    }

    public static int lineCount(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        int count = 0;
        while(input.hasNextLine()){
            count++;
            input.nextLine();
        }
        return count;
    }      

    public static int byteCount(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        int count = 0;
        while(input.hasNextLine()){
            String line = input.nextLine();
            Scanner lineScan = new Scanner(line);
            count += line.length() + 1; // add one for new line character
        }
        return count;
    }
}
