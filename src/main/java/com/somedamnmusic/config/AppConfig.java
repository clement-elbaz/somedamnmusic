package com.somedamnmusic.config;



import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.servlet.SessionScoped;
import com.google.sitebricks.SitebricksModule;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.MailService;
import com.somedamnmusic.database.redis.RedisDatabaseService;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.LoginJob;
import com.somedamnmusic.jobs.LoginJob.LoginJobFactory;
import com.somedamnmusic.jobs.PostMusicJob;
import com.somedamnmusic.jobs.PostMusicJob.PostMusicJobFactory;
import com.somedamnmusic.jobs.PostMusicOnFeedJob;
import com.somedamnmusic.jobs.PostMusicOnFeedJob.PostMusicOnFeedJobFactory;
import com.somedamnmusic.jobs.SimpleJobService;
import com.somedamnmusic.jobs.UpdateUserJob;
import com.somedamnmusic.jobs.UpdateUserJob.UpdateUserJobFactory;
import com.somedamnmusic.mail.MailServiceImpl;
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
			                
			                bind(Session.class).in(SessionScoped.class);
			                
			                install(new FactoryModuleBuilder()
			                .implement(LoginJob.class, LoginJob.class)
			                .build(LoginJobFactory.class));
			                
			                install(new FactoryModuleBuilder()
			                .implement(PostMusicJob.class, PostMusicJob.class)
			                .build(PostMusicJobFactory.class));
			                
			                install(new FactoryModuleBuilder()
			                .implement(PostMusicOnFeedJob.class, PostMusicOnFeedJob.class)
			                .build(PostMusicOnFeedJobFactory.class));
			                
			                install(new FactoryModuleBuilder()
			                .implement(UpdateUserJob.class, UpdateUserJob.class)
			                .build(UpdateUserJobFactory.class));
			                
			                bind(MailService.class).to(MailServiceImpl.class);
			                bind(String.class).annotatedWith(Names.named("email.user")).toInstance(System.getenv("SDM_EMAIL_USER"));
			                bind(String.class).annotatedWith(Names.named("email.password")).toInstance(System.getenv("SDM_EMAIL_PASSWORD"));
			                
			                
			                bind(DatabaseService.class).to(RedisDatabaseService.class);
			                bind(String.class).annotatedWith(Names.named("database.host")).toInstance(System.getenv("SDM_DB_HOST"));
			                bind(Integer.class).annotatedWith(Names.named("database.port")).toInstance(Integer.parseInt(System.getenv("SDM_DB_PORT")));
			                bind(String.class).annotatedWith(Names.named("database.client")).toInstance(System.getenv("SDM_DB_CLIENT"));
			                bind(String.class).annotatedWith(Names.named("database.password")).toInstance(System.getenv("SDM_DB_PASSWORD"));
			                
			                bind(JobService.class).to(SimpleJobService.class);
			            }
				}
		);
		return injector;
	}

}
