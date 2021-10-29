package cn.com.kun.component.redo.core;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 作用：扫描框架代码里的dao层接口，注册bean
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
public class InnerMapperBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private String[] basePackages = new String[]{"cn.com.kun.component.redo.dao",
            "cn.com.kun.component.clusterlock.dblock.dao"};

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        //这一句是必须加的，注册过滤器，在扫描bean定义会匹配到加了@Mapper注解的类
        scanner.registerFilters();
        scanner.doScan(basePackages);
    }
}
