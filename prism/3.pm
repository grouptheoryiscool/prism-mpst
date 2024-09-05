module p

        s_p : [0..4];
        end_p : bool init false;
        m_p : [0..6] init 0;

        [p_q_m1] s_p=3 -> (s_p'=2);
        [p_q_m2] s_p=4 -> (s_p'=2);
        [pq] s_p=2 & (send = false) -> [0.2,0.3] : (s_p'=3) & (m_p'=1) + [0.7,0.8] : (s_p'=4) & (m_p'=2);
        [r_p_m1] s_p=1 & m_r=1 -> (s_p'=2);
        [s_p_m1] s_p=0 & m_s=1 -> (s_p'=1);

endmodule

module q

        s_q : [0..5];
        end_q : bool init false;

        [pq] s_q=4 -> (s_q'=5);
        [rq] s_q=0 -> (s_q'=1);
        [sq] s_q=2 -> (s_q'=3);
        [p_q_m1] s_q=5 & (m_p = 1) -> (s_q'=4);
        [p_q_m2] s_q=5 & (m_p = 2) -> (s_q'=4);
        [s_q_m1] s_q=3 -> (s_q'=4);
        [r_q_m1] s_q=1 -> (s_q'=2);

endmodule

module r

        s_r : [0..4];
        end_r : bool init false;
        m_r : [0..6] init 0;

        [r_q_m1] s_r=3 -> (end_r'=true) & (s_r'=4);
        [] s_r=2 -> [1,1] : (s_r'=3);
        [r_p_m1] s_r=1 -> (s_r'=2);
        [] s_r=0 -> [1,1] : (s_r'=1);

endmodule

module s

        s_s : [0..4];
        end_s : bool init false;
        m_s : [0..6] init 0;

        [s_q_m1] s_s=3 -> (end_s'=true) & (s_s'=4);
        [] s_s=2 -> [1,1] : (s_s'=3);
        [s_p_m1] s_s=1 -> (s_s'=2);
        [] s_s=0 -> [1,1] : (s_s'=1);

endmodule

formula send = (s_p = 1) | (s_r = 1);


