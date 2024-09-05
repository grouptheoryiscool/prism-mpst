Modules File generated:

global send : [0..4] init 0;
global m : [0..6] init 0;

module p

        s_p : [0..4];
        end_p : bool init false;

        [p!q_m1] s_p=3 -> (s_p'=2);
        [p!q_m2] s_p=4 -> (s_p'=2);
        [] s_p=2 -> [0.2,0.3] : (s_p'=3) & (send = 1) & (m=1) + [0.7,0.8] : (s_p'=4) & (send = 1) & (m=1);
        [r!p_m1] s_p=1 & m=1 -> (s_p'=2);
        [s!p_m1] s_p=0 & m=1 -> (s_p'=1);

endmodule

module q

        s_q : [0..2];
        end_q : bool init false;

        [p!q_m1] s_q=2 -> (s_q'=2);
        [p!q_m2] s_q=2 -> (s_q'=2);
        [s!q_m1] s_q=1 -> (s_q'=2);
        [r!q_m1] s_q=0 -> (s_q'=1) & send=;

endmodule

module r

        s_r : [0..4];
        end_r : bool init false;

        [r!q_m1] s_r=3 -> (end_r'=true) & (s_r'=4);
        [] s_r=2 -> [1,1] : (s_r'=3);
        [r!p_m1] s_r=1 -> (s_r'=2);
        [] s_r=0 -> [1,1] : (s_r'=1);

endmodule

module s

        s_s : [0..4];
        end_s : bool init false;

        [s!q_m1] s_s=3 -> (end_s'=true) & (s_s'=4);
        [] s_s=2 -> [1,1] : (s_s'=3);
        [s!p_m1] s_s=1 -> (s_s'=2);
        [] s_s=0 -> [1,1] : (s_s'=1);

endmodule



problem -
global variable updates
sending is blocked, suppose p receives from s first and then r.
then if r takes its [] step first, this communication is blocked



