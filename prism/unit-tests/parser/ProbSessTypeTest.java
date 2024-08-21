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
import java.util.*;

import jdk.jfr.Timestamp;
import prism.PrismTranslationException;


public class ProbSessTypeTest {

    private static PrismParser parser;
    private static ModulesFile mf1;
    private static ModulesFile mf2;


    @BeforeClass
    public static void setUp() throws PrismLangException, PrismTranslationException, FileNotFoundException {
        parser = new PrismParser();
        InputStream str1 = new FileInputStream("./unit-tests/parser/TranslationSimple.txt");
        InputStream str2 = new FileInputStream("./unit-tests/parser/TranslationRec.txt");
        mf1 = parser.parseTypeEnv(str1).toModulesFile();
        mf1.tidyUp();
        System.out.println("Module Files generated: \n" + mf1.toString());
        //mf2 = parser.parseTypeEnv(str2).toModulesFile();
    }

    @Test 
    public void typingEnvBranch() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvBranch.txt");
            result = parser.parseTypeEnv(str);
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
            result = parser.parseTypeEnv(str);
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
            result = parser.parseTypeEnv(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        HashMap<ChannelType, ProbSessType> entries = result.getEntries();
        assertEquals(3, result.getSize());
        for (ChannelType chan : entries.keySet()) {
            assertEquals(chan.getSession(), "s");
            if (chan.getRole().equals("p")) {
                ProbSel probSel = (ProbSel) entries.get(chan);
                assertEquals(probSel.getRole(), "q");
            } else if (chan.getRole().equals("r")) {
                Branching branches = (Branching) entries.get(chan);
                assertEquals(2, branches.getOptions().size());
            } else if (chan.getRole().equals("q")) {
                Branching branching = (Branching) entries.get(chan);
                assertEquals(branching.getRole(), "p");
                ProbSessType branchTwo = branching.getOptions().get(1).getContinuation();
                ProbSel branchSel = (ProbSel) branchTwo;
                assertEquals(branchSel.getRole(), "r");
            } else {
                throw new PrismLangException("Invalid role: " + chan.getRole());
            }
        }
    }

    @Test 
    public void typingEnvNested() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvNested.txt");
            result = parser.parseTypeEnv(str);
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

    @Test 
    public void typingEnvRec() throws PrismLangException {
        InputStream str = null;
        TypeEnv result = null;
        try {
            str = new FileInputStream("./unit-tests/parser/typingEnvRec.txt");
            result = parser.parseTypeEnv(str);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (PrismLangException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        }
        HashMap<ChannelType, ProbSessType> entries = result.getEntries();
        assertEquals(2, result.getSize());
        for (ChannelType chan : entries.keySet()) {
            assertEquals(chan.getSession(), "s");
            if (chan.getRole().equals("p")) {
                RecSessType rectypeOne = (RecSessType) entries.get(chan);
                Branching branching = (Branching) rectypeOne.getBody();
                RecVar recvar = (RecVar) branching.getOptions().get(0).getContinuation();
                assertEquals(recvar.getRecVar(), "x");
                assertEquals(branching.getOptions().get(1).getLabel(), "l2");
            } else if (chan.getRole().equals("r")) {
                Branching branches = (Branching) entries.get(chan);
                RecvBranch rectypeTwo = (RecvBranch) branches.getOptions().get(0);
                RecSessType recMsgType = (RecSessType) rectypeTwo.getMsgType();
                ProbSel recTypeBody = (ProbSel) recMsgType.getBody();
                assertEquals(recTypeBody.getBranches().get(0).getLabel(), "l4");
            } else {
                throw new PrismLangException("Invalid role: " + chan.getRole());
            }
        }
    }

    @Test
    public void moduleNames() {
        String [] genmodnames = mf1.getModuleNames();
        Arrays.sort(genmodnames);
        String[] modnames = {"p", "q", "r"};
        assertArrayEquals(modnames, genmodnames);
    }

    @Test
    public void statesAndEnd() throws PrismTranslationException {
        for (int i = 0; i < 2; i++) {
            parser.ast.Module m = mf1.getModule(i);
            if (m.getName().equals("p")) {
                assertEquals(m.getNumCommands(), 6);
                for (int j = 0; j < 6; j++) {
                    Command c = m.getCommand(j);
                    Updates us = c.getUpdates();
                    if (c.getSynch().equals("p!r_m4")) {
                        assertEquals(us.getNumUpdates(), 1);
                        Update u = us.getUpdate(0);
                        assertEquals(u.getNumElements(), 2);
                        for (int k = 0; k < 2; k++) {
                            UpdateElement ue = u.getElement(k);
                            if (ue.getVar().equals("end_p")) {
                                ExpressionLiteral val = (ExpressionLiteral) ue.getExpression();
                                assertEquals(val.getValue(), true);
                            } else if (ue.getVar().equals("s_p")) {
                                ExpressionLiteral val = (ExpressionLiteral) ue.getExpression();
                                assertEquals(val.getValue(), 4);
                            } else {
                                fail();
                            }
                        }
                    }
                }
            } else if (m.getName().equals("r")) {
                assertEquals(m.getNumCommands(), 6);
                for (int j = 0; j < 6; j++) {
                    Command c = m.getCommand(j);
                    Updates us = c.getUpdates();
                    assertEquals(us.getNumUpdates(), 1);
                    Update u = us.getUpdate(0);
                    if (c.getSynch().equals("p!r_m5")) {
                        assertEquals(u.getNumElements(), 1);
                        UpdateElement ue = u.getElement(0);
                        assertEquals(ue.getVar(), "s_r");
                        ExpressionLiteral val = (ExpressionLiteral) ue.getExpression();
                        assertEquals(val.getValue(), 4);
                    }
                }
            } else if (m.getName().equals("q")) {
                assertEquals(m.getNumCommands(), 7);
                for (int j = 0; j < 7; j++) {
                    Command c = m.getCommand(j);
                    if (c.getSynch().equals("q!p_m3")) {
                        Updates us = c.getUpdates();
                        assertEquals(us.getNumUpdates(), 1);
                        Update u = us.getUpdate(0);
                        assertEquals(u.getNumElements(), 2);
                        for (int k = 0; k < 2; k++) {
                            UpdateElement ue = u.getElement(k);
                            if (ue.getVar().equals("end_q")) {
                                ExpressionLiteral val = (ExpressionLiteral) ue.getExpression();
                                assertEquals(val.getValue(), true);
                            } else if (ue.getVar().equals("s_q")) {
                                ExpressionLiteral val = (ExpressionLiteral) ue.getExpression();
                                assertEquals(val.getValue(), 9);
                            } else {
                                fail();
                            }
                        }
                    }
                }
            } else { fail(); }
        }
    }
}