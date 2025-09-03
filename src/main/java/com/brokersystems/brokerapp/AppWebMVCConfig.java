package com.brokersystems.brokerapp;


import com.brokersystems.brokerapp.audit.SpringSecurityAuditorAware;
import com.brokersystems.brokerapp.reports.config.CustomJasperReportFormatView;
import com.brokersystems.brokerapp.server.datatables.DataTableRequestResolver;
import com.brokersystems.brokerapp.server.utils.DateConverter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.siegmar.fastcsv.reader.CsvReader;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.easybatch.core.job.JobExecutor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@ImportResource({"classpath:activiti-config.xml"})
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.brokersystems.brokerapp" })
@EnableJpaRepositories(basePackages = { "com.brokersystems.brokerapp" })
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableSpringDataWebSupport
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = false)
@EnableCaching
@EnableAsync
@PropertySource({"classpath:config.properties","classpath:mail.properties"})
public class AppWebMVCConfig extends WebMvcConfigurerAdapter {

	
	@Autowired
	Environment env;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new DateConverter());
	}

	@Bean
	public WebContentInterceptor getWebContentInterceptor(){
		WebContentInterceptor interceptor = new WebContentInterceptor();
		 interceptor.setCacheSeconds(0);
		 interceptor.setCacheControl(CacheControl.noCache());
		 return interceptor;
	}

	@Bean
	public CsvReader getCsvReader(){
		return new CsvReader();
	}


	@Bean
	public JasperReportsViewResolver getJasperReportsViewResolver() {
	  JasperReportsViewResolver resolver = new JasperReportsViewResolver();
		if(StringUtils.equalsIgnoreCase(databaseType(),"oracle")){
			resolver.setPrefix("classpath:/oracle_reports/");
		}
		else if(StringUtils.equalsIgnoreCase(databaseType(),"mssql")){
			resolver.setPrefix("classpath:/mssql/");
		}
		else{
			resolver.setPrefix("classpath:/reports/");
		}
	  resolver.setSuffix(".jasper");
	  resolver.setReportDataKey("datasource");
	  resolver.setViewNames("rpt_*");
	  resolver.setViewClass(CustomJasperReportFormatView.class);
	  resolver.setOrder(0);
	  return resolver;
	}

	@Bean
	public InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setPrefix("/WEB-INF/pages/");
		bean.setSuffix(".jsp");
		bean.setOrder(2);
		return bean;
	} 
	@Bean
	public TilesConfigurer TilesConfigurer()
	{
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions("/WEB-INF/tiles.xml");
		return tilesConfigurer;
	}
	@Bean
	public UrlBasedViewResolver viewResolver() {
		UrlBasedViewResolver bean = new UrlBasedViewResolver();
		bean.setViewClass(TilesView.class);
		bean.setOrder(1);
		return bean;
	} 


	@Bean
	public static PropertyPlaceholderConfigurer placeHolderConfigurer() {
		PropertyPlaceholderConfigurer bean = new PropertyPlaceholderConfigurer();
		bean.setLocation(new ClassPathResource("config.properties"));
		bean.setLocation(new ClassPathResource("mail.properties"));
		bean.setIgnoreUnresolvablePlaceholders(true);
		return bean;
	}


//	@Bean(name = "dataSource")
//	public DataSource dataSource() throws NamingException {
//		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
//		dsLookup.setResourceRef(true);
//		DataSource dataSource = dsLookup.getDataSource("jdbc/brokerDS");
//		return dataSource;
//	}



	@Bean(name = "dataSource")
	public DataSource dataSource() {

		String driverClassName = env.getProperty("jdbc.driverClassName");
		String databaseurl = env.getProperty("jdbc.databaseurl");
		String dbUsername = env.getProperty("jdbc.username");
		String dbPassword = env.getProperty("jdbc.password");
		int poolSize =Integer.parseInt(env.getProperty("max.poolsize"));
		boolean autoCommit =Boolean.parseBoolean(env.getProperty("connection.autocommit"));
		HikariDataSource bean = new HikariDataSource();
		bean.setDriverClassName(driverClassName);
		bean.setJdbcUrl(databaseurl);
		bean.setUsername(dbUsername);
		bean.setPassword(dbPassword);
		bean.setMaximumPoolSize(poolSize);
		bean.setLeakDetectionThreshold(30000);
		bean.setAutoCommit(autoCommit);
		return bean;
	}

	@Bean(name="restTemplate")
	public RestTemplate restTemplate() {
		CloseableHttpClient httpClient = HttpClients.custom()
				.disableCookieManagement()
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectTimeout(3000000); // 10 seconds
		requestFactory.setConnectionRequestTimeout(3000000); // 10 seconds
		requestFactory.setReadTimeout(3000000); // 10 seconds
		return new RestTemplate(requestFactory);
	}

	@Bean
	public Properties hibernateProperties() {
		String dialect = env.getProperty("jdbc.dialect");
		String showSql = env.getProperty("hibernate.show_sql");
		String formatSql = env.getProperty("hibernate.format_sql");
		String hbm2ddl = env.getProperty("hibernate.hbm2ddl.auto");
		String namingStrategy = env.getProperty("hibernate.naming_strategy");
		String cachingEnabled = env.getProperty("hikari.implicitCachingEnabled");
		String maxStatements = env.getProperty("dataSource.maxStatements");
		String autoCommit = env.getProperty("connection.autocommit");
		String autoDetection = env.getProperty("hibernate.autodetection");
		String genstatistics = env.getProperty("hibernate.generate_statistics");
		String useCache = env.getProperty("hibernate.cache.use_second_level_cache");
		String cacheClass = env.getProperty("hibernate.cache.region.factory_class");
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", dialect);
		hibernateProperties.setProperty("hibernate.show_sql", showSql);
		hibernateProperties.setProperty("hibernate.format_sql", formatSql);
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
		hibernateProperties.setProperty("hibernate.ejb.naming_strategy",namingStrategy);
		hibernateProperties.setProperty("hikari.dataSource.implicitCachingEnabled",cachingEnabled);
		hibernateProperties.setProperty("hikari.dataSource.maxStatements",maxStatements);
		hibernateProperties.setProperty("hibernate.connection.autocommit",autoCommit);
		hibernateProperties.setProperty("hibernate.archive.autodetection",autoDetection);
		hibernateProperties.setProperty("hibernate.generate_statistics",genstatistics);
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache",useCache);
		hibernateProperties.setProperty("hibernate.cache.region.factory_class",cacheClass);
		hibernateProperties.setProperty("hibernate.max_fetch_depth","25");
		hibernateProperties.setProperty("hibernate.leakDetectionThreshold ","60000");
		return hibernateProperties;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory sessionFactory(DataSource dataSource,
			Properties hibernateProperties) {
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(
				dataSource);
        sessionBuilder.scanPackages("com.brokersystems.brokerapp");
		sessionBuilder.addProperties(hibernateProperties);
		sessionBuilder.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
		return sessionBuilder.buildSessionFactory();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()throws NamingException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("com.brokersystems.brokerapp");
		em.setPersistenceUnitName("archPersistenceUnit");
		em.setJpaProperties(hibernateProperties());
		return em;
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory emf) {
		//JtaTransactionManager transactionManager = new JtaTransactionManager();
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}


	@Bean(name="database_type")
	public String databaseType(){
		return env.getProperty("database_type");
	}
	

	@Bean
	public SpringSecurityAuditorAware springSecurityAuditorAware() {
		return new SpringSecurityAuditorAware();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		BCryptPasswordEncoder bean = new BCryptPasswordEncoder(10);
		return bean;
	}


	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/login").setViewName("login");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/app-assets/**").addResourceLocations("/app-assets/");
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean(name = "messageSource")
	public ReloadableResourceBundleMessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
		resource.setBasename("classpath:i18n/messages");
		resource.setDefaultEncoding("UTF-8");
		resource.setFallbackToSystemLocale(true);
		return resource;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setDefaultLocale(Locale.ENGLISH);
		localeResolver.setCookieName("locale");
		localeResolver.setCookieMaxAge(Integer.MAX_VALUE);
		return localeResolver;
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
	    org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(104857600);
		multipartResolver.setMaxInMemorySize(52428800);
	    return multipartResolver;
	}

	@Bean
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		return objectMapper;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	@Override
	public void configureMessageConverters(
			java.util.List<org.springframework.http.converter.HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter bean = new MappingJackson2HttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		List<MediaType> mediaTypess = new ArrayList<>();
		mediaTypess.add(MediaType.APPLICATION_XML);
		mediaTypess.add(MediaType.TEXT_PLAIN);
		mediaTypess.add(MediaType.APPLICATION_JSON);
		stringHttpMessageConverter.setSupportedMediaTypes(mediaTypess);
		bean.setPrettyPrint(true);
        List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.IMAGE_JPEG);
		mediaTypes.add(MediaType.IMAGE_PNG);
		mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		ByteArrayHttpMessageConverter messageConverter = new ByteArrayHttpMessageConverter();
		messageConverter.setSupportedMediaTypes(mediaTypes);
		converters.add(bean);
	}
	@Override
	public void addArgumentResolvers(
			java.util.List<org.springframework.web.method.support.HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new DataTableRequestResolver());
	}
	
	@Bean
	public JobExecutor getJobExecutor(){
		return new JobExecutor();
	}
	
//	@Bean
//    public JavaMailSender getMailSender(){
//		String host = env.getProperty("mail.host");
//		String port = env.getProperty("mail.port");
//		String username = env.getProperty("sender.username");
//		String password = env.getProperty("sender.password");
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost(host);
//        mailSender.setPort(new Integer(port));
//        mailSender.setUsername(username);
//        mailSender.setPassword(password);
//        Properties javaMailProperties = new Properties();
////        javaMailProperties.put("mail.smtp.starttls.enable", "true");
//        javaMailProperties.put("mail.smtp.auth", "true");
//        javaMailProperties.put("mail.transport.protocol", "smtp");
////        javaMailProperties.put("mail.debug", "true");
//        javaMailProperties.put("mail.smtp.ehlo", "false");
//        mailSender.setJavaMailProperties(javaMailProperties);
//        return mailSender;
//    }

	@Bean
	public JavaMailSender getMailSender(){
		String host = env.getProperty("mail.host");
		String port = env.getProperty("mail.port");
		String username = env.getProperty("sender.username");
		String password = env.getProperty("sender.password");
		String ttlsenable = env.getProperty("starttls.enable");
		String ttlsrequired = env.getProperty("starttls.required");
		String ehlo = env.getProperty("mail.smtp.ehlo");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(new Integer(port));
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		Properties javaMailProperties = new Properties();
		if(Boolean.valueOf(ttlsenable)) {
	     	javaMailProperties.put("mail.smtp.starttls.enable", "true");
		}
		if(Boolean.valueOf(ttlsrequired)) {
			javaMailProperties.put("mail.smtp.starttls.required", "true");
		}
		javaMailProperties.put("mail.smtp.auth", "true");
		if(!Boolean.valueOf(ehlo)) {
			javaMailProperties.put("mail.smtp.ehlo", "false");
		}
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		javaMailProperties.put("mail.debug", "true");
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}
	
	@Bean
	public VelocityEngine velocityEngine() throws VelocityException, IOException{
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		Properties props = new Properties();
		props.put(Velocity.RESOURCE_LOADER, "string");
		props.put("string.resource.loader.class",StringResourceLoader.class.getName());
		props.put("string.resource.loader.repository.static", "false");
		props.put(Velocity.RESOURCE_LOADER,"class");
		props.put("class.resource.loader.class", ClasspathResourceLoader.class.getName());
		factory.setVelocityProperties(props);
		return factory.createVelocityEngine();
	}


	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cmfb.setShared(true);
		return cmfb;
	}


}