package cn.com.kun.component.redo.core;

import org.apache.commons.lang3.ClassUtils;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 作用：扫描框架代码里的dao层接口，注册bean
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
/*
 加下面的注解都没用，无法控制Import的顺序
 */
//@DependsOn("autoConfiguredMapperScannerRegistrar")
//@AutoConfigureAfter(MybatisAutoConfiguration.MapperScannerRegistrarNotFoundConfiguration.class)
//@Order(Ordered.LOWEST_PRECEDENCE)
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
        scanner.doScan(getAllBasePackages(registry));
        //假如后续有其他jar包需要扫描，可以额外使用@MapperScan,重复扫描不会有影响
    }

    private String[] getAllBasePackages(BeanDefinitionRegistry registry) {

        BeanDefinition beanDefinition = registry.getBeanDefinition("application");
        //启动类
        String applicationBeanClass = beanDefinition.getBeanClassName();
        //获取到启动类所在的包名，MybatisAutoConfiguration的自动配置默认扫描的就是basePackage
        //做一步的目的是为了将失效的自动配置扫描行为重新生效
        String basePackage = ClassUtils.getPackageName(applicationBeanClass);
        String[] newbasePackages = new String[basePackages.length + 1];
        for (int i = 0; i < basePackages.length; i++) {
            newbasePackages[i] = basePackages[i];
        }
        newbasePackages[basePackages.length] = basePackage;
        return newbasePackages;
    }

}
