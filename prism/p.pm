mdp

module p 

    s_p : [0..10] init 0;

    [p_q_m1] (s_p = 0) -> [0.2,0.3] : (s_p'=1) + [0.7,0.8] : (s_p'=2);

endmodule