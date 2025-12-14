USE [QuanLyVeTau]
GO
--Xóa bản chỗ đặt
use QuanLyVeTau
DELETE FROM ChoDat;

-- I. CHÈN CHỖ NGỒI GHẾ MỀM (4 Toa x 64 Ghế = 256 Chỗ)
-- Giả sử tồn tại ít nhất 64 hàng trong sys.columns để sinh số
WITH Numbers (STT) AS (
    -- Sinh số thứ tự từ 1 đến 64
    SELECT TOP 64 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) FROM sys.columns c1, sys.columns c2
),
GhếMềm AS (
    SELECT
        STT,
        -- Phân chia 64 ghế vào 16 "Khoang" (khu vực 4 ghế)
        CEILING(CAST(STT AS DECIMAL) / 4) AS Khoang
    FROM Numbers
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [LoaiCho], [Khoang], [Tang])
SELECT
    -- MaCho: [STT]-[MaToa]-C, ví dụ: SPT2-1-C01
    t.MaToa + '-' + 'C' + FORMAT(gm.STT, '00') AS MaCho,
    t.MaToa,
    FORMAT(gm.STT, '00') AS SoCho, -- SoCho là số thứ tự 01, 02, ... 64
    N'Ghế mềm' AS LoaiCho,
    gm.Khoang,
    NULL AS Tang
FROM GhếMềm gm
-- Áp dụng cho 4 toa ghế mềm
CROSS JOIN (VALUES (N'SPT2-1'), (N'SPT2-2'), (N'SPT2-3'), (N'SPT2-4')) AS t(MaToa);

-- =============================================================
-- II. CHÈN CHỖ NGỒNG GIƯỜNG NẰM (4 Toa x 42 Giường = 168 Chỗ)
-- Giường nằm: 7 Khoang x 6 Giường/Khoang (3 tầng, 2 vị trí A/B)
-- =============================================================

WITH Params AS (
    SELECT 42 AS MaxCho, 'AB' AS SideLetters -- 42 giường tối đa/toa
),
Numbers (N) AS (
    -- Sinh số thứ tự từ 1 đến 42
    SELECT TOP 42 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) FROM sys.columns c1, sys.columns c2
),
GiườngNằm AS (
    SELECT 
        N,
        -- Tính toán Khoang (1-7)
        (N - 1) / 6 + 1 AS Khoang,                      
        -- Tính toán Tầng (1=dưới, 2=giữa, 3=trên)
        ((N - 1) % 6) / 2 + 1 AS Tang,                
        -- Tính toán vị trí (A hoặc B) - Giữ lại để tính toán Khoang/Tầng
        SUBSTRING(SideLetters, ((N - 1) % 2) + 1, 1) AS Side 
    FROM Numbers
    CROSS JOIN Params
)
INSERT INTO [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [LoaiCho], [Khoang], [Tang])
SELECT
    -- MaCho: [MaToa]-C[STT], ví dụ: SPT2-5-C01
    t.MaToa + N'-C' + FORMAT(gn.N, '00') AS MaCho,
    
    t.MaToa,
    
    -- *** LOGIC TẠO SOCHO ĐÃ SỬA THÀNH SỐ THỨ TỰ (STT) ***
    FORMAT(gn.N, '00') AS SoCho, 
    -- ****************************************************
    
    N'Giường nằm' AS LoaiCho,
    gn.Khoang,
    gn.Tang
FROM GiườngNằm gn
-- Áp dụng cho 4 toa giường nằm
CROSS JOIN (VALUES (N'SPT2-5'), (N'SPT2-6'), (N'SPT2-7'), (N'SPT2-8')) AS t(MaToa);

GO