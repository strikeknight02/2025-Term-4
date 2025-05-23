/*
    This file was generated automatically by Alchitry Labs 2.0.30-BETA.
    Do not edit this file directly. Instead edit the original Lucid source.
    This is a temporary file and any changes made to it will be destroyed.
*/

module arith_right_shifter (
        input wire [31:0] a,
        input wire [31:0] b,
        output reg [31:0] out
    );
    logic [4:0] M_mux_s0;
    logic [4:0][31:0] M_mux_i0;
    logic [4:0][31:0] M_mux_i1;
    logic [4:0][31:0] M_mux_out;
    
    genvar idx_0_1724689683;
    
    generate
        for (idx_0_1724689683 = 0; idx_0_1724689683 < 5; idx_0_1724689683 = idx_0_1724689683 + 1) begin: forLoop_idx_0_1724689683
            mux_2 mux (
                .s0(M_mux_s0[idx_0_1724689683]),
                .i0(M_mux_i0[idx_0_1724689683]),
                .i1(M_mux_i1[idx_0_1724689683]),
                .out(M_mux_out[idx_0_1724689683])
            );
        end
    endgenerate
    
    
    logic [31:0] wr;
    logic [31:0] xr;
    logic [31:0] yr;
    logic [31:0] zr;
    always @* begin
        M_mux_i0[1'h0] = a;
        M_mux_i1[1'h0] = {{5'h10{a[5'h1f]}}, a[5'h1f:5'h10]};
        M_mux_s0[1'h0] = b[3'h4];
        wr = M_mux_out[1'h0];
        M_mux_i0[1'h1] = wr;
        M_mux_i1[1'h1] = {{4'h8{a[5'h1f]}}, wr[5'h1f:4'h8]};
        M_mux_s0[1'h1] = b[2'h3];
        xr = M_mux_out[1'h1];
        M_mux_i0[2'h2] = xr;
        M_mux_i1[2'h2] = {{3'h4{a[5'h1f]}}, xr[5'h1f:3'h4]};
        M_mux_s0[2'h2] = b[2'h2];
        yr = M_mux_out[2'h2];
        M_mux_i0[2'h3] = yr;
        M_mux_i1[2'h3] = {{2'h2{a[5'h1f]}}, yr[5'h1f:2'h2]};
        M_mux_s0[2'h3] = b[1'h1];
        zr = M_mux_out[2'h3];
        M_mux_i0[3'h4] = zr;
        M_mux_i1[3'h4] = {a[5'h1f], zr[5'h1f:1'h1]};
        M_mux_s0[3'h4] = b[1'h0];
        out = M_mux_out[3'h4];
    end
    
    
endmodule