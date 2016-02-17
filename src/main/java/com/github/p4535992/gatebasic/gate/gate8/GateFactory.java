package com.github.p4535992.gatebasic.gate.gate8;

import gate.Controller;
import gate.Factory;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.UrlResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 13/02/2016.
 * @author 4535992.
 */
public class GateFactory {

    /**
     If you need to duplicate other resources, use the two-argument
     Factory.duplicate, passing the ctx as the second parameter, to preserve object graph
     two calls to Factory.duplicate(r, ctx) for the same resource r in the same context ctx will return the same duplicate.
     calls to the single argument Factory.duplicate(r) or to the
     two-argument version with different contexts will return different duplicates.
     Can call the default duplicate algorithm (bypassing the CustomDuplication check) via Factory.defaultDuplicate
     it is safe to call defaultDuplicate(this, ctx), but calling duplicate(this, ctx) from within its own custom
     duplicate will cause infinite recursion!
     */
    public Resource duplicate(Factory.DuplicationContext ctx, List<ProcessingResource> prList)throws ResourceInstantiationException {
        // duplicate this controller in the default way - this handles subclasses nicely
        Controller c = (Controller) Factory.defaultDuplicate((Resource) this, ctx);
        // duplicate each of our PRs
        List<ProcessingResource> newPRs = new ArrayList<>();
        for(ProcessingResource pr : prList) {
            newPRs.add((ProcessingResource)Factory.duplicate(pr, ctx));
        }
        // and set this duplicated list as the PRs of the copy
        c.setPRs(newPRs);
        return c;
    }

    /**
     * @return a {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer}
     * so that placeholders are correctly populated
     * @throws Exception exception if the file is not found or cannot be opened or read
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws Exception {
        PropertySourcesPlaceholderConfigurer propConfig = new PropertySourcesPlaceholderConfigurer();
        org.springframework.core.io.Resource[] resources = new UrlResource[]
                {new UrlResource("file:${user.home}/.config/api.properties")};
        propConfig.setLocations(resources);
        propConfig.setIgnoreResourceNotFound(true);
        propConfig.setIgnoreUnresolvablePlaceholders(true);
        return propConfig;
    }
}
