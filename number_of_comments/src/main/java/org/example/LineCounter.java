package org.example;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class LineCounter {
    public static void listClasses(File projectDir, File destinationFile) throws FileNotFoundException {
        try {
            FileWriter outputfile = new FileWriter(destinationFile);
            CSVWriter writer = new CSVWriter(outputfile, CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            String[] header = {
                    "Class",
                    "Number of commentsComment",
                    "Number of line comments",
                    "Number of Block comments" ,
                    "Number of Javadoc comments",
                    "Number of Licence comments",
                    "Number of comments in methode",
                    "Number of method comments",
                    "Number of class comments"};
            writer.writeNext(header);

            new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
                System.out.println(path);
                System.out.println("--------------------");
                JavaParser javaParser = new JavaParser();
                ParseResult<CompilationUnit> compilationUnit = javaParser.parse(file);

                int licence = 0;
                int lineComments = 0;
                int methodComments = 0;
                int classComments = 0;

                for( var comment : compilationUnit.getCommentsCollection().get().getComments()){
                    try {
                        var parent = getParentNode(comment);
                        if(parent.isEmpty()){
                            licence += 1;
                        } else if (parent.get().getClass().equals(CompilationUnit.class)){
                            classComments += 1;
                        } else if (parent.get().getClass().equals(ClassOrInterfaceDeclaration.class) ){
                            methodComments += 1;
                        } else {
                            lineComments += 1;
                        } //class java.util.NoSuchElementException
                    } catch(NoSuchElementException e){
                        licence = -1;
                        lineComments = -1;
                        methodComments = -1;
                        classComments = -1;
                        break;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                String[] data = {
                        path,
                        Integer.toString(compilationUnit.getCommentsCollection().get().getComments().size()),
                        Integer.toString(compilationUnit.getCommentsCollection().get().getLineComments().size()),
                        Integer.toString(compilationUnit.getCommentsCollection().get().getBlockComments().size()),
                        Integer.toString(compilationUnit.getCommentsCollection().get().getJavadocComments().size()),
                        Integer.toString(compilationUnit.getCommentsCollection().get().getJavadocComments().size()),
                        licence < 0? "null":Integer.toString(licence),
                        licence < 0? "null":Integer.toString(lineComments),
                        licence < 0? "null":Integer.toString(methodComments),
                        licence < 0? "null":Integer.toString(classComments)
                };
                writer.writeNext(data);
            }).explore(projectDir);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static Optional<Node> getParentNode(Comment comment) {
        Optional<Node> node = comment.getCommentedNode();
        if (node.isPresent()){
            return node.get().getParentNode();
        }
        node = comment.getParentNode();
        return node.get().getParentNode();
    }

    //./gradlew run --args="../../projects/spark/ ../../Analysis/spark/comments_in_code.csv"
    public static void main(String[] args) throws FileNotFoundException {
        File projectDir = new File("../../apache/kafka/");
        File destinationFile = new File("../../Analysis/kafka/comments_in_code.csv");
        if(args.length > 1) {
            projectDir = new File(args[0]);
            destinationFile = new File(args[1]);
        }
        listClasses(projectDir, destinationFile);
    }
}
