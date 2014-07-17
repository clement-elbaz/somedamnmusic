package com.somedamnmusic.config;



import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.servlet.SessionScoped;
import com.google.sitebricks.SitebricksModule;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.MailService;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.dumb.DumbMailService;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.JobServiceImpl;
import com.somedamnmusic.jobs.LoginJob;
import com.somedamnmusic.jobs.LoginJob.LoginJobFactory;
import com.somedamnmusic.jobs.PostMusicJob;
import com.somedamnmusic.jobs.PostMusicJob.PostMusicJobFactory;
import com.somedamnmusic.pages.MainPage;
import com.somedamnmusic.session.Session;

public class AppConfig extends com.google.inject.servlet.GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		Injector injector = Guice.createInjector ( 
				new SitebricksModule() {
			         protected void configureSitebricks() {
			                // scan class Example's package and all descendants
			                scan(MainPage.class.getPackage());
			                //Injection value of message
			                bindConstant().annotatedWith(Names.named("message")).to("Hello Woooorld");
			                
			                bind(Session.class).in(SessionScoped.class);
			                
			                install(new FactoryModuleBuilder()
			                .implement(LoginJob.class, LoginJob.class)
			                .build(LoginJobFactory.class));
			                
			                install(new FactoryModuleBuilder()
			                .implement(PostMusicJob.class, PostMusicJob.class)
			                .build(PostMusicJobFactory.class));
			                
			                bind(MailService.class).to(DumbMailService.class);
			                bind(DatabaseService.class).to(DumbDatabase.class);
			                
			                bind(JobService.class).to(JobServiceImpl.class);
			            }
				}
		);
		return injector;
	}

}
