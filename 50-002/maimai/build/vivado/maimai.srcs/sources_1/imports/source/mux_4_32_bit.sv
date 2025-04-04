/*
    This file was generated automatically by Alchitry Labs 2.0.30-BETA.
    Do not edit this file directly. Instead edit the original Lucid source.
    This is a temporary file and any changes made to it will be destroyed.
*/

module mux_4_32_bit (
        input wire s0,
        input wire s1,
        input wire [3:0][31:0] in,
        output reg [31:0] out
    );
    always @* begin
        
        case ({s1, s0})
            2'h0: begin
                out = in[1'h0];
            end
            2'h1: begin
                out = in[1'h1];
            end
            2'h2: begin
                out = in[2'h2];
            end
            2'h3: begin
                out = in[2'h3];
            end
            default: begin
                out = 1'h0;
            end
        endcase
    end
    
    
endmodule