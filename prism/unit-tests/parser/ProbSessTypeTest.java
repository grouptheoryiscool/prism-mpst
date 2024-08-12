import org.junit.Test;
import static org.junit.Assert.*;
import parser.ast.*;
import parser.PrismParser;
import prism.PrismLangException;
import org.junit.BeforeClass;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.channels.Channel;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProbSessTypeTest {

    private static PrismParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new PrismParser();
    }

    @Test 
    public void typingEnvBranch() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvBranch.txt");
            result = parser.ParseSessionType(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        assertEquals(1, result.getSize());
        Map.Entry<ChannelType, ProbSessType> entry = result.getEntries().entrySet().iterator().next();
        ChannelType chan = entry.getKey();
        ProbSessType pst = entry.getValue();
        assertEquals(chan.getRole(), "q");
        assertEquals(chan.getSession(), "s");
        Branching branching = (Branching) pst;
        assertEquals(branching.getRole(), "p");
        assertEquals(branching.getOptions().size(), 2);
        RecvBranch branchOne = branching.getOptions().get(0);
        RecvBranch branchTwo = branching.getOptions().get(1);
        assertEquals(branchOne.getLabel(), "m");
        assertEquals(branchTwo.getLabel(), "n");
        BaseType baseTypeOne = (BaseType) branchOne.getMsgType();
        BaseType baseTypeTwo = (BaseType) branchTwo.getMsgType();
        assertEquals(baseTypeOne.getBasicType(), BaseType.basictype.INT);
        assertEquals(baseTypeTwo.getBasicType(), BaseType.basictype.BOOL);
        assertTrue(branchOne.getContinuation() instanceof TypeEnd);
        assertTrue(branchTwo.getContinuation() instanceof ProbSel);
    }

    @Test 
    public void typingEnvSel() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvSel.txt");
            result = parser.ParseSessionType(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        assertEquals(1, result.getSize());
        Map.Entry<ChannelType, ProbSessType> entry = result.getEntries().entrySet().iterator().next();
        ChannelType chan = entry.getKey();
        ProbSessType pst = entry.getValue();
        assertEquals(chan.getRole(), "p");
        assertEquals(chan.getSession(), "s");
        ProbSel probSel = (ProbSel) pst;
        assertEquals(probSel.getRole(), "q");
        assertEquals(probSel.getBranches().size(), 2);
        SelBranch branchOne = probSel.getBranches().get(0);
        SelBranch branchTwo = probSel.getBranches().get(1);
        assertEquals(branchOne.getLabel(), "m");
        assertEquals(branchTwo.getLabel(), "m");
        BaseType baseTypeOne = (BaseType) branchOne.getMsgType();
        BaseType baseTypeTwo = (BaseType) branchTwo.getMsgType();
        assertEquals(baseTypeOne.getBasicType(), BaseType.basictype.INT);
        assertEquals(baseTypeTwo.getBasicType(), BaseType.basictype.BOOL);
        assertTrue(branchOne.getContinuation() instanceof TypeEnd);
        assertTrue(branchTwo.getContinuation() instanceof TypeEnd);
    }


    @Test 
    public void typingEnvSimple() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvSimple.txt");
            result = parser.ParseSessionType(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        HashMap<ChannelType, ProbSessType> entries = result.getEntries();
        assertEquals(3, result.getSize());
        for (ChannelType chan : entries.keySet()) {
            assertEquals(chan.getSession(), "s");
            if (chan.getRole() == "p") {
                ProbSel probSel = (ProbSel) entries.get(chan);
                assertEquals(probSel.getRole(), "q");
            } else if (chan.getRole() == "r") {
                Branching branches = (Branching) entries.get(chan);
                assertEquals(2, branches.getOptions().size());
            } else if (chan.getRole() == "q") {
                Branching branching = (Branching) entries.get(chan);
                assertEquals(branching.getRole(), "p");
                ProbSessType branchTwo = branching.getOptions().get(1).getContinuation();
                ProbSel branchSel = (ProbSel) branchTwo;
                assertEquals(branchSel.getRole(), "r");
            }
        }
    }

    @Test 
    public void typingEnvNested() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvNested.txt");
            result = parser.ParseSessionType(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        Map.Entry<ChannelType, ProbSessType> entry = result.getEntries().entrySet().iterator().next();
        ProbSel pst = (ProbSel) entry.getValue();
        SelBranch branchOne = pst.getBranches().get(0);
        SelBranch branchTwo = pst.getBranches().get(1);
        ProbSel msgOne = (ProbSel) branchOne.getMsgType();
        Branching msgTwo = (Branching) branchTwo.getMsgType();
        assertEquals(msgOne.getBranches().get(0).getMsgType().toString(), "INT");
        assertEquals(msgOne.getBranches().get(1).getLabel(), "m");
        assertTrue(branchOne.getContinuation() instanceof TypeEnd);
        assertTrue(branchTwo.getContinuation() instanceof TypeEnd);
        assertEquals(msgTwo.getOptions().get(0).getMsgType().toString(), "BOOL");
        assertEquals(msgTwo.getOptions().get(1).getLabel(), "m2");
    }

}