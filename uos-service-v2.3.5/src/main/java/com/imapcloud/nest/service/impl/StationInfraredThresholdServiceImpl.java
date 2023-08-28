package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.StationInfraredThresholdEntity;
import com.imapcloud.nest.mapper.StationInfraredThresholdMapper;
import com.imapcloud.nest.service.StationInfraredThresholdService;
import com.imapcloud.nest.utils.RestRes;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-13
 */
@Service
public class StationInfraredThresholdServiceImpl extends ServiceImpl<StationInfraredThresholdMapper, StationInfraredThresholdEntity> implements StationInfraredThresholdService {

	private double[][] datas = {{-30,0},{-18,1},{0,2},{12,3},{20,3.5},{28,4},{30,4.2},
			{40,5},{51,6},{61,7},{71,8},{78,9},{85,10}};

	private double[] ranges = {-18,0,12,20,28,30,40,51,61,71,78};

    @Override
    public void setTemperature(Double temperature) {
        Double value = 0.02 * temperature + 1;
        this.lambdaUpdate().eq(StationInfraredThresholdEntity::getName,"表计阈值")
                .set(StationInfraredThresholdEntity::getTemperature,temperature)
                .set(StationInfraredThresholdEntity::getValue,value)
                .update();
    }

	@Override
	public RestRes getList(StationInfraredThresholdEntity stationInfraredThresholdEntity) {
		List<StationInfraredThresholdEntity> lists = this.list(
				new QueryWrapper<StationInfraredThresholdEntity>(stationInfraredThresholdEntity));
		Map<String,Object> param = new HashMap<>();
		param.put("list", lists);
		return RestRes.ok(param);
	}
	
	/**更新阈值
	 * -当获取到的温度有值时，优先通过温度计算出值1的值作为更新依据
	 */
	@Override
	public RestRes updateData(StationInfraredThresholdEntity stationInfraredThresholdEntity) {
		 if(stationInfraredThresholdEntity.getId() == null)
		 	return RestRes.err("id == null");
		 StationInfraredThresholdEntity tmp = this.getById(stationInfraredThresholdEntity);
		 if(tmp == null)
		 	return RestRes.err("no record");
		 if(stationInfraredThresholdEntity.getTemperature() != null && (
		 		tmp.getTemperature() == null || Math.abs(stationInfraredThresholdEntity.getTemperature() - tmp.getTemperature()) > 0.000001) ) {
			 //stationInfraredThresholdEntity.setMeterNum(0.02 * stationInfraredThresholdEntity.getTemperature() + 1);
			 stationInfraredThresholdEntity.setMeterNum(tempertureTometerNum(stationInfraredThresholdEntity.getTemperature()));
		 }
		 if(this.updateById(stationInfraredThresholdEntity))
			 return RestRes.ok();
		 return RestRes.err("update fail");
	}

	private double tempertureTometerNum(double temperature) {
		double value = 1;
		for(int i = 0;i <= ranges.length;i ++){
			if(( i -1 < 0 && ranges[i] > temperature )
					||(i == ranges.length && temperature >= ranges[i - 1])
					||(i -1 >= 0 && ranges[i] > temperature && ranges[i - 1] <= temperature)){
				return tempertureTometerNum1(datas,i,i+1,temperature);
			}
		}

		return value;
	}

	private double tempertureTometerNum1(double[][] datas,int start,int end,double temperature) {
		double value = (temperature - datas[start][0]) / (datas[end][0] - datas[start][0])
				* (datas[end][1] - datas[start][1]) + datas[start][1];
		return value;
	}
}
