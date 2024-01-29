package org.example.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@ComponentScan
@Configuration
@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        servlet.setContextConfigLocation("org.example");
        // Log đường dẫn đăng ký
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }
    @Bean("example")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema yourSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("YourWebServiceName");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://yournamespace.com");
        wsdl11Definition.setSchema(yourSchema);
        return wsdl11Definition;
    }


    @Bean
    public XsdSchema yourSchema() {
        return new SimpleXsdSchema(new ClassPathResource("your-schema.xsd"));
    }
//    @Bean
//    public EndpointInterceptor customSecurityInterceptor() {
//        return new CustomSecurityInterceptor();
//    }
//
//    @Override
//    public void addInterceptors(List<EndpointInterceptor> interceptors) {
//        interceptors.add(customSecurityInterceptor());
//    }
}
