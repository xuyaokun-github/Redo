package cn.com.kun.component.redo.core;

import org.apache.commons.lang3.ClassUtils;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
/*
 加下面的注解都没用，无法控制Import的顺序
 */
//@DependsOn("autoConfiguredMapperScannerRegistrar")
//@AutoConfigureAfter(MybatisAutoConfiguration.MapperScannerRegistrarNotFoundConfiguration.class)
//@Order(Ordered.LOWEST_PRECEDENCE)
public class RedoInnerMapperBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private String[] basePackages = new String[]{"cn.com.kun.component.redo.dao"};

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
        //这2句是必须加的，注册过滤器，在扫描bean定义会匹配到加了@Mapper注解的类
        scanner.setAnnotationClass(Mapper.class);
        scanner.registerFilters();
        scanner.doScan(getAllBasePackages(registry));
        //假如后续有其他jar包需要扫描，可以额外使用@MapperScan,重复扫描不会有影响
    }

    private String[] getAllBasePackages(BeanDefinitionRegistry registry) {


        String[] beanNames = ((DefaultListableBeanFactory) registry).getBeanNamesForAnnotation(SpringBootApplication.class);
        String beanName = beanNames[0];
        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        //启动类类名
        String applicationBeanClass = beanDefinition.getBeanClassName();
        //获取到启动类所在的包名，MybatisAutoConfiguration的自动配置默认扫描的就是basePackage
        //做这一步是为了将失效的自动配置扫描行为重新生效
        String basePackage = ClassUtils.getPackageName(applicationBeanClass);
        //让mybatis多扫描两个路径
        String[] newbasePackages = new String[basePackages.length + 1];
        for (int i = 0; i < basePackages.length; i++) {
            newbasePackages[i] = basePackages[i];
        }
        newbasePackages[basePackages.length] = basePackage;
        return newbasePackages;
    }

}
