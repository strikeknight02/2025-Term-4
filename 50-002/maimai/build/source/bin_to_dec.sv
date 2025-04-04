/*
    This file was generated automatically by Alchitry Labs 2.0.30-BETA.
    Do not edit this file directly. Instead edit the original Lucid source.
    This is a temporary file and any changes made to it will be destroyed.
*/

module bin_to_dec #(
        parameter DIGITS = 3'h4,
        parameter LEADING_ZEROS = 1'h0
    ) (
        input wire [($clog2((64'(4'ha) ** (DIGITS))))-1:0] value,
        output reg [(DIGITS)-1:0][3:0] digits
    );
    logic [31:0] R_1ba85608_j;
    logic [31:0] RR_1ba85608_j;
    logic [31:0] R_1b16b90b_i;
    logic [31:0] RR_1b16b90b_i;
    logic [($bits(value))-1:0] L_73d8e029_remainder;
    logic L_73d8e029_blank;
    logic [($bits(value))-1:0] L_53a4e62b_scale;
    logic [($bits(value))-1:0] L_524067d6_sub_value;
    always @* begin
        digits = {DIGITS{{{4'hb}}}};
        L_73d8e029_remainder = value;
        L_73d8e029_blank = !LEADING_ZEROS;
        if (value < (64'(4'ha) ** (DIGITS))) begin
            for (RR_1ba85608_j = 0; RR_1ba85608_j < DIGITS; RR_1ba85608_j = RR_1ba85608_j + 1) begin
        R_1ba85608_j = (DIGITS - 1'h1) + RR_1ba85608_j * (-2'sh1);
                L_53a4e62b_scale = (64'(4'ha) ** (R_1ba85608_j));
                if (L_73d8e029_remainder < L_53a4e62b_scale) begin
                    if (R_1ba85608_j != 1'h0 && L_73d8e029_blank) begin
                        digits[R_1ba85608_j] = 4'ha;
                    end else begin
                        digits[R_1ba85608_j] = 1'h0;
                    end
                end else begin
                    L_73d8e029_blank = 1'h0;
                    L_524067d6_sub_value = 1'h0;
                    for (RR_1b16b90b_i = 0; RR_1b16b90b_i < 4'h9; RR_1b16b90b_i = RR_1b16b90b_i + 1) begin
            R_1b16b90b_i = (4'h9) + RR_1b16b90b_i * (-2'sh1);
                        if (L_73d8e029_remainder < (R_1b16b90b_i + 1'h1) * L_53a4e62b_scale) begin
                            digits[R_1ba85608_j] = R_1b16b90b_i;
                            L_524067d6_sub_value = R_1b16b90b_i * L_53a4e62b_scale;
                        end
                    end
                    L_73d8e029_remainder = L_73d8e029_remainder - L_524067d6_sub_value;
                end
            end
        end
    end
    
    
endmodule