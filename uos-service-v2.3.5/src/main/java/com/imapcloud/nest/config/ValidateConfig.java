//package com.imapcloud.nest.config;
//
//import org.hibernate.validator.HibernateValidator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
//
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//
///**
// * 校验器配置
// *
// * @author wmin
// */
//@Configuration
//public class ValidateConfig {
//
//    @Bean
//    public Validator validator() {
//        ValidatorFactory validatorFactory = Validation
//                .byProvider(HibernateValidator.class)
//                .configure()
//                // true-快速失败返回模式    false-普通模式
//                .addProperty("hibernate.validator.fail_fast", "true")
//                .buildValidatorFactory();
//        return validatorFactory.getValidator();
//    }
//
//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor() {
//        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
//        /**设置validator模式为快速失败返回*/
//        postProcessor.setValidator(validator());
//        return postProcessor;
//    }
//
//}
