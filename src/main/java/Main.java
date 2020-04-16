import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


class  Main {
    public static void main(String args[]) throws IOException {
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("When you enter the path of your json file, \"fileName-out\" directory will be created in same path.\nDirectory will contain dot-file, png, svg and pdf of the parse tree of your json.");
        System.out.print("Enter the path:\t");
        String inputPath = scanner.nextLine();

        // get file name without extension
        String fileNameWithOutExt = new File(inputPath).getName().replaceFirst("[.][^.]+$", "");

        // get the path of "out" directory
        Path path = Paths.get(inputPath);
        String parentPath = path.getParent().toString() + "/" + fileNameWithOutExt + "-out/";

        // make the "out" directory
        new File(parentPath).mkdir();

        // generate the dot file in the "out" directory
        ASTGenerator.generateDOTFile(inputPath, parentPath + "/parsetree.dot");

        // generate png, svg, pdf
        // png: dot -Tpng /home/example/json-out/graph.dot -o /home/example/json-out/graph.png
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (OSValidator.isWindows()) {
            processBuilder.command("cmd.exe", "/c",
                    "c:\\release\\bin\\dot.exe -Tpng " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.png & " +
                    "c:\\release\\bin\\dot.exe -Tsvg " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.svg & " +
                    "c:\\release\\bin\\dot.exe -Tpdf " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.pdf "
                );
            Process process = processBuilder.start();
        } else if (OSValidator.isMac()) {
            System.out.println("Mac is not supported!");
        } else if (OSValidator.isUnix()) {
            processBuilder.command("bash", "-c",
                    "dot -Tpng " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.png ; " +
                    "dot -Tsvg " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.svg ; " +
                    "dot -Tpdf " + parentPath +"/parsetree.dot -o " + parentPath + "/parsetree.pdf"
                );
            Process process = processBuilder.start();
        } else if (OSValidator.isSolaris()) {
            System.out.println("Solaris is not support!");
        } else {
            System.out.println("Your OS is not support!!");
        }
    }
}