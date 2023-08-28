package com.imapcloud.nest.controller;


/**
 * <p>
 * 违建点信息表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-03-17
 * @deprecated 2.2.3，已跟前端确认，当前类中所有接口均废弃，请勿使用，将在后续版本删除
 */
@Deprecated
//@RestController
//@RequestMapping("/illegalPoint")
public class IllegalPointController {

//    @Autowired
//    private IllegalPointService illegalPointService;
//    @Autowired
//    private DataMultispectralService dataMultispectralService;
//    @Autowired
//    private DataOrthoService dataOrthoService;
//
////    @GetMapping("/")
////    public RestRes getPointCloudTagList(Integer source) {
////        return illegalPointService.getTagIdByFile(source);
////    }
////
////
////    @GetMapping("/point/cloud/list")
////    public RestRes getPointCloudList(Integer tagId, Integer source) {
////        return illegalPointService.getPointCloudList(tagId, source);
////    }
//
//    @GetMapping("/illegal/point/list")
//    public RestRes getIllegalPointList(Integer beforeFileId, Integer afterFileId, Integer problemSource) {
//        return illegalPointService.getIllegalPointList(beforeFileId, afterFileId, problemSource);
//    }
//
//    @GetMapping("/hz/illegal/point/list")
//    public RestRes getHZIllegalPointList(Integer problemSource) {
//        return illegalPointService.getHZIllegalPointList(problemSource);
//    }
//
//    @PostMapping("/save/illegal/point")
//    @ResponseBody
//    public RestRes saveIllegalPoint(@RequestBody List<IllegalPointEntity> illegalPointEntityList) {
//        return illegalPointService.saveIllegalPoint(illegalPointEntityList);
//    }
//
//    @PostMapping("/update/illegal/point")
//    @ResponseBody
//    public RestRes updateIllegalPoint(@RequestBody IllegalPointEntity illegalPointEntity) {
//        return illegalPointService.updateIllegalPoint(illegalPointEntity);
//    }
//
//    @PostMapping("/delete")
//    public RestRes deleteIllegalPoint(@RequestBody Integer[] ids) {
//        return illegalPointService.deleteIllegalPoint(Arrays.asList(ids));
//    }
//
//    @GetMapping("/smart/analysis")
//    public RestRes smartAnalysis(Integer beforeFileId, Integer afterFileId, String threshold, String kmlUrl) {
//        return illegalPointService.smartAnalysis(beforeFileId, afterFileId, threshold, kmlUrl);
//    }
//
//    @GetMapping("/stop/smart/analysis")
//    public RestRes stopSmartAnalysis(Integer beforeFileId, Integer afterFileId) {
//        return illegalPointService.stopSmartAnalysis(beforeFileId, afterFileId);
//
//    }
//
//
//    /**
//     * 分析中台-违建识别-标签点云列表
//     * @return
//     */
//    @GetMapping("/getTagPointCloudList")
//    public RestRes getTagPointCloudList() {
//        Map map = illegalPointService.getTagPointCloudList();
//        return RestRes.ok(map);
//    }
//
//    /**
//     *
//     * 分析中台-违建识别-标签正射列表
//     * @return
//     */
//    @GetMapping("/getTagOrthoList")
//    public RestRes getTagOrthoList(Integer problemSource) {
//        Map map = illegalPointService.getTagOrthoList(problemSource);
//        return RestRes.ok(map);
//    }
//
//    /**
//     * 问题清单
//     * @param problemSource
//     * @return
//     */
//    @GetMapping("/getTagOrthoRecordList")
//    public RestRes getTagOrthoRecordList(Integer problemSource,String recordMonth,String recordDay) {
//        Map map = illegalPointService.getTagOrthoRecordList(problemSource,recordMonth,recordDay);
//        return RestRes.ok(map);
//    }
//
//    /**
//     * 智能匹配照片
//     * @param id
//     * @param dataType
//     * @param problemSource
//     * @param tagId
//     * @param latitude
//     * @param longitude
//     * @param range
//     * @return
//     */
//    @GetMapping("/autoMatchPhoto")
//    public RestRes autoMatchPhoto(Integer id, Integer dataType, Integer problemSource, Integer tagId, Double latitude, Double longitude, Double range) {
//        return illegalPointService.autoMatchPhoto(id, dataType, problemSource, tagId, latitude, longitude, range);
//    }
//
//    @GetMapping("/getHistoryPhotoList")
//    public RestRes getHistoryPhotoList(Integer dataId, Integer taskId, Long photoId, Integer dataType, Integer problemSource, Integer tagId, Double range, String startTime, String endTime) {
//        Map map = illegalPointService.getHistoryPhotoList(dataId, taskId, photoId, dataType, problemSource, tagId, range, startTime, endTime);
//        return RestRes.ok(map);
//    }
//
//    @GetMapping("/getRelatedPhoto")
//    public RestRes getRelatedPhoto(Integer afterFileId, Integer dataType) {
//        Map map = illegalPointService.getRelatedPhoto(afterFileId, dataType);
//        return  RestRes.ok(map);
//    }
//
//    @PostMapping("/markPhoto")
//    public RestRes markPhoto(@RequestBody IllegalPointMarkDTO illegalPointMarkDTO) {
//        illegalPointService.markPhoto(illegalPointMarkDTO);
//        return RestRes.ok();
//    }
//
//    @PostMapping("/deleteData")
//    public RestRes deleteDataList(@RequestBody DeleteDataDTO deleteDataDTO) {
//        illegalPointService.deleteDataList(deleteDataDTO);
//        return RestRes.ok();
//    }
//
//
//    /**
//     * 识别地物
//     * @param id
//     * @param type
//     * @return
//     */
//    @GetMapping("/analysisFeatures")
//    public RestRes analysisFeatures(Integer id,Integer type){
//        return illegalPointService.analysFeatures(id,type);
//    }
//
//    /**
//     * 识别水质反演
//     * @param id
//     * @return
//     */
//    @GetMapping("/analysisWater")
//    public RestRes analysisWater(Integer id){
//        return illegalPointService.analysWater(id);
//    }
//
////    /**
////     * @deprecated 2.2.3，已跟前端确认该接口未在使用，将在后续版本删除
////     */
////    @Deprecated
////    @GetMapping("/delFeatures")
////    public RestRes delFeatures(Integer id){
////        illegalPointService.delFeatures(id);
////        return RestRes.ok();
////    }
////    /**
////     * @deprecated 2.2.3，已跟前端确认该接口未在使用，将在后续版本删除
////     */
////    @Deprecated
////    @GetMapping("/delWater")
////    public RestRes delWater(Integer id){
////        illegalPointService.delWater(id);
////        return RestRes.ok();
////    }
//
//    @GetMapping("/test")
//    public RestRes analysTest(){
//        return illegalPointService.analysTest();
//    }
//
//    /**
//     * 获取多光谱-水质反演
//     * @param id
//     * @return
//     */
//    @GetMapping("/getMultispectral")
//    public RestRes getMultispectral(Integer id){
//        Map map = dataMultispectralService.getAllMap(id);
//        return RestRes.ok(map);
//    }
//
//    /**
//     * 获取正射-地物识别
//     * @param id
//     * @return
//     */
//    @GetMapping("/getOrtho")
//    public RestRes getOrtho(Integer id){
//        Map map = dataOrthoService.getAllMap(id);
//        return RestRes.ok(map);
//    }
//
//
//    /**
//     * 下载水质
//     * @param id
//     * @param response
//     */
//    @GetMapping("/downLoadWater")
//    public void downLoadWater(Integer id, HttpServletResponse response){
//        illegalPointService.downLoadWater(id,response);
//    }

}

