/*
 * Library crate supporting wc, Rust reimagining of wc.
 */


// the runtime environment
use std::{env, io};
// running the program could return an error so we use the Error interface
use std::error::Error;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::process::exit;


// A generic result with a dynamically allocated error.
// Dynamically allocated so that it's lifetime is longer than
// the function where it was declared.
type CmdResult<ReturnType> = Result<ReturnType, Box<dyn Error>>;

static mut L_OPTION: bool = false;
static mut W_OPTION: bool = false;
static mut C_OPTION: bool = false;


pub fn run() {
    unsafe { // for mut static variables

        let command_line: Vec<String> = env::args().collect();
        let args = &command_line[1..];
        for mut i in 0..args.len() { 
            if args[i].starts_with("-") {
                if !args[i].eq("-v") && !args[i].eq("--version") && !args[i].eq("-h") && !args[i].eq("--help"){
                    i = i + 1;
                    let temp_file = File::open(&args[i]).expect("could not find file");
                }
            } else {
                let temp_file = File::open(&args[i]).expect("could not find file");
            }
        }

        let mut total_words = 0;
        let mut total_lines = 0;
        let mut total_bytes = 0;
        let mut number_of_files = 0;
        
        if args[0].starts_with("--") {
            number_of_files = args.len() - 1;
            let long_flag = &args[0][2..];
            if long_flag.eq("version") {
                println!(env!("CARGO_PKG_VERSION"));
                exit(0);
            }
            if long_flag.eq("help"){
                help_message();
                exit(0);
            }
            for i in 1..args.len() {
                if long_flag.eq("words") {
                    let w_count = word_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", w_count);
                    println!("{}", args[i]);
                    total_words += w_count;
                    W_OPTION = true;
                }
                if long_flag.eq("lines") {
                    let l_count = line_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", l_count);
                    println!("{}", args[i]);
                    total_lines += l_count;
                    L_OPTION = true;
                }
                if long_flag.eq("bytes") {
                    let b_count = byte_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", b_count);
                    println!("{}", args[i]);
                    total_bytes += b_count;
                    C_OPTION = true;
                }
                if !long_flag.eq("words") && !long_flag.eq("lines") && !long_flag.eq("bytes") { // could move outside of for loop
                    panic!("couldn't identify long flag");
                }
            }
        } else if args[0].starts_with("-") {
            number_of_files = args.len() - 1;
            let short_flag = &args[0][1..];
            if short_flag.eq("v") {
                println!(env!("CARGO_PKG_VERSION"));
                exit(0);
            }
            if short_flag.eq("h") {
                help_message();
                exit(0);
            }
            for i in 1..args.len() {
                if short_flag.eq("w") {
                    let w_count = word_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", w_count);
                    println!("{}", args[i]);
                    total_words += w_count;
                    W_OPTION = true;
                }
                if short_flag.eq("l") {
                    let l_count = line_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", l_count);
                    println!("{}", args[i]);
                    total_lines += l_count;
                    L_OPTION = true;
                }
                if short_flag.eq("c") {
                    let b_count = byte_count(open(&args[i]).expect("couldn't open file"));
                    print!("{}    ", b_count);
                    println!("{}", args[i]);
                    total_bytes += b_count;
                    C_OPTION = true;
                }
                if !short_flag.eq("w") && !short_flag.eq("l") && !short_flag.eq("c") { // could move outside of for loop
                    panic!("couldn't identify short flag");
                }
            }
        } else {
            W_OPTION = true;
            L_OPTION = true;
            C_OPTION = true;
            for i in 0..args.len() {
                let w_count = word_count(open(&args[i]).expect("couldn't open file"));
                let l_count = line_count(open(&args[i]).expect("couldn't open file"));
                let c_count = byte_count(open(&args[i]).expect("couldn't open file"));
                print!("{}    ", w_count);
                total_words = total_words + w_count;
                print!("{}    ", l_count);
                total_lines = total_lines + l_count;
                print!("{}    ", c_count);
                total_bytes = total_bytes + c_count;
                println!("{}", args[i]);
            }
        } 
        if number_of_files > 1 { // at very end, print the totals
            if W_OPTION {
                print!("{}    ", total_words);
            }
            if L_OPTION {
                print!("{}    ", total_lines);
            }
            if C_OPTION {
                print!("{}    ", total_bytes);
            }
            println!("total");
        }
    } // end of unsafe 
} // end of run


pub fn help_message(){ 
    println!("Options:");
    println!("    -v, --version   Show version and exit");
    println!("    -h, --help      Display this message and exit");
    println!("    -w, --words     Show word count");
    println!("    -l, --lines     Show line count");
    println!("    -c, --bytes     Show byte count");
    println!("    [FILENAME]...   Show word, line, and byte count");       
}

/**
 * Open the named file; special handling of "-" for stdin.
 */
fn open(filename: &str) -> CmdResult<Box<dyn BufRead>> {
    match filename {
        //"-" => Ok(Box::new(BufReader::new(io::stdin()))),
        _ => Ok(Box::new(BufReader::new(File::open(filename)?))),
    }
}

pub fn line_count(handle: Box<dyn BufRead>) -> i32 {
    let mut num_lines = 0;
    for _line in handle.lines() {
        num_lines += 1;
    }
    num_lines
}

pub fn word_count(handle: Box<dyn BufRead>) -> usize {
    let mut num_words = 0;
    for element in handle.lines() {
        num_words += element.unwrap().split_whitespace().count();
    }
    num_words
}

pub fn byte_count(handle: Box<dyn BufRead>) -> usize {
    let mut num_bytes = 0;
    for element in handle.lines() {
        num_bytes += element.unwrap().len() + 1; // add one for \n
    }
    num_bytes
}

