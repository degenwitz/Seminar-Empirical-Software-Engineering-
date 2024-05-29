package org.example;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.comments.Comment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        String classText = "public class Dummy{\n" + "//Comment\n" + "}";
        StringReader reader = new StringReader(classText);
        InputStream targetStream = new ByteArrayInputStream(classText.getBytes());
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> compilationUnit = javaParser.parse(targetStream);

        for( var a : compilationUnit.getCommentsCollection().get().getComments()){
            System.out.println( a.getContent() );
        }
    }
}