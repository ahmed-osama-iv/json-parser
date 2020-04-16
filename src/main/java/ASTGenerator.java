/*
    By Satnam Sandhu from this link:
    https://stackoverflow.com/questions/48435911/generate-ast-in-the-form-of-a-dot-file
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.nio.charset.Charset;
import java.nio.file.Files;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

class ASTGenerator {

    private static ArrayList<String> LineNum;
    private static ArrayList<String> Type;
    private static ArrayList<String> Content;

    private static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, Charset.forName("UTF-8"));
    }

    public static void generateDOTFile(String inputFilePath, String outputFilePath) throws IOException{

        LineNum = new ArrayList<String>();
        Type = new ArrayList<String>();
        Content = new ArrayList<String>();

        String inputString = readFile(inputFilePath);
        ANTLRInputStream input = new ANTLRInputStream(inputString);
        JSONLexer lexer = new JSONLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokens);
        ParserRuleContext ctx = parser.json();

        generateAST(ctx, false, 0);

        String graph = "digraph G { \n" + getDOT() + "\n" + "}";

        try {
            FileWriter myWriter = new FileWriter(outputFilePath);
            myWriter.write(graph);
            myWriter.close();
            System.out.println("Generating DOT-file done successfully.");
        } catch (IOException e) {
            System.out.println("Generating DOT-file has an Error!");
            e.printStackTrace();
        }
    }

    private static void generateAST(RuleContext ctx, boolean verbose, int indentation) {
        boolean toBeIgnored = !verbose && ctx.getChildCount() == 1 && ctx.getChild(0) instanceof ParserRuleContext;

        if (!toBeIgnored) {
            String ruleName = JSONParser.ruleNames[ctx.getRuleIndex()];
            LineNum.add(Integer.toString(indentation));
            Type.add(ruleName);
            Content.add(ctx.getText());
        }
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree element = ctx.getChild(i);
            if (element instanceof RuleContext) {
                generateAST((RuleContext) element, verbose, indentation + (toBeIgnored ? 0 : 1));
            }
        }
    }

    private static String getDOT(){
        String output = getLabel();
        int pos = 0;
        for(int i=1; i<LineNum.size();i++){
            pos=getPos(Integer.parseInt(LineNum.get(i))-1, i);
            output += ((Integer.parseInt(LineNum.get(i))-1)+Integer.toString(pos)+"->"+LineNum.get(i)+i) + "\n";
        }
        return output;
    }

    private static String getLabel(){
        String output = "";
        for(int i =0; i<LineNum.size(); i++){
            output += (LineNum.get(i)+i+"[label=\""+Type.get(i)+"\\n "+Content.get(i).replace("\"", "\\\"")+" \"]") + "\n";
        }
        return output;
    }

    private static int getPos(int n, int limit){
        int pos = 0;
        for(int i=0; i<limit;i++){
            if(Integer.parseInt(LineNum.get(i))==n){
                pos = i;
            }
        }
        return pos;
    }
}