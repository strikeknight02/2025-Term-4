/*
    This file was generated automatically by Alchitry Labs 2.0.30-BETA.
    Do not edit this file directly. Instead edit the original Lucid source.
    This is a temporary file and any changes made to it will be destroyed.
*/

module compare_last_bit (
        input wire [31:0] a,
        input wire [31:0] b,
        output reg [31:0] out
    );
    always @* begin
        out = (6'h20)'((a[1'h0] == b[1'h0]));
    end
    
    
endmodule