package com.phillypham.ka.infection;

import java.io.*;

public class Infector {

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.exit(-1);
		}
		BufferedReader fin = new BufferedReader(new FileReader(args[0]));
		PrintWriter cout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));		
		cout.println(fin.readLine());
		fin.close();
		cout.close();
	}

}
