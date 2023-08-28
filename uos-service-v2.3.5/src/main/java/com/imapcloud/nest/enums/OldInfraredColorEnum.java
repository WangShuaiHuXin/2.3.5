package com.imapcloud.nest.enums;

import java.util.stream.Stream;

/**
 * @author sjx
 * @data : 2022/3/22 17:49
 */
//@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum OldInfraredColorEnum {
	WHITE_HOT("白热", "WHITE_HOT","#000000,#FFFFFF",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	COLOR_1("熔岩","COLOR_1","#050000,#FF3200,#FFFF1F",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	IRONBOW_1("铁红","IRONBOW_1","#000000,#540084,#FF5C00,#FFFFA4",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	COLOR_2("热铁","COLOR_2","#050000,#008484,#F9FC02,#E30700",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	GREEN_HOT("医疗","GREEN_HOT","#000000,#D83EE1,#1F2DFE,#12EBE2,#01A100,#FFF714,#FF3C3C,#FFEDED",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	ICE_FIRE("北极","ICE_FIRE","#000000,#1F2DFE,#01A100,#FFF714,#FF3C3C,#FC0BFC",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	RAINBOW("彩虹","RAINBOW","#810002,#D83EE1,#1F2DFE,#12EBE2,#0BFF00,#FFF714,#FF3C3C",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	RAIN("彩虹","RAIN","#1F2DFE,#00FEFE,#FFF714,#FF1100,#880000",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	RED_HOT("描红","RED_HOT","#000000,#FDFDFD,#EF0D0D",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),
	BLACK_HOT("黑热","BLACK_HOT","#FFFFFF,#000000",new String[]{"default","Phantom 3 Advanced Camera", "Phantom 3 Professional Camera", "Phantom 3 Standard Camera", "Phantom 3 4K Camera", "Phantom", "Phantom 4 RTK Camera", "Phantom 4 advanced Camera","Phantom 4 PRO Camera", "Phantom 4 PV2 Camera", "Phantom 4 Camera", "Mavic", "Mavic PRO Camera","Mavic 2 Enterprise", "Mavic 2 PRO Camera", "Mavic 2 Zoom Camera","M300", "Spark Camera", "X5","X5R", "X5S","X4S","Z3","Z30","X7","Sequoia mono", "Sequoia RGB", "Zenmuse H20", "Zenmuse H20T","Zenmuse p1", "Zenmuse p1 24mm", "Zenmuse p1 35mm", "Zenmuse p1 50mm","Mavic 2 Enterprise Dual-Visual", "Mavic 2 Enterprise Advanced"}),

	HOT_SPOT("灼热","HOT_SPOT","#A01301,#EC8B16,#8C8C8C,#070C08",new String[]{ "Mavic 2 Enterprise Dual-Visual"}),
	RAINBOW2("彩虹","RAINBOW2","#F8E0C5,#F7253A,#FCE51C,#23B812,#0066DB",new String[]{ "Mavic 2 Enterprise Dual-Visual"}),
	GRAY("灰度","GRAY", "#FAFAFA,#000000", new String[]{ "Mavic 2 Enterprise Dual-Visual"}),
	HOT_METAL("铁红","HOT_METAL","#FFFFA4,#FBCD2B,#FF5C00,#91009F,#0B0084,#000000",new String[]{ "Mavic 2 Enterprise Dual-Visual"}),

	COLD_SPOT("医疗","COLD_SPOT","#101010,#7A7A7A,#00A8FE,#132A94",new String[]{ "Mavic 2 Enterprise Dual-Visual"}),
	;
	

	private String name;
	private String key;
	private String code;
	private String[] cameraList;

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getCode() {
		return code;
	}



	public void setName(String name) {
		this.name = name;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String[] getCameraList() {
		return cameraList;
	}

	public void setCameraList(String[] cameraList) {
		this.cameraList = cameraList;
	}


	OldInfraredColorEnum(String name, String key, String code, String[] cameraList){
		this.name = name;
		this.key = key;
		this.code = code;
		this.cameraList = cameraList;
	}

	public static OldInfraredColorEnum getInstanceByKey(String colorKey){
		if (colorKey != null) {
			OldInfraredColorEnum[] values = OldInfraredColorEnum.values();
			for (OldInfraredColorEnum ic : values) {
				if (eqIgnoreCaseAndSpace(ic.getKey(), colorKey)) {
					return ic;
				}
			}
		}
		return null;
	}
	public static boolean eqIgnoreCaseAndSpace(String str1, String str2) {
		if (str1 != null && str2 != null) {
			String replace1 = str1.replace(" ", "").toUpperCase();
			String replace2 = str2.replace(" ", "").toUpperCase();
			return replace1.equals(replace2);
		}
		return false;
	}

	public static Stream<OldInfraredColorEnum> stream(){
		return Stream.of(OldInfraredColorEnum.values());
	}
}

