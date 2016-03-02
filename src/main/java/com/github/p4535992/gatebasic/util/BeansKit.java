package com.github.p4535992.gatebasic.util;

import javax.annotation.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 21/04/2015.
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
public class BeansKit implements  org.springframework.context.ResourceLoaderAware,BeanPostProcessor {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( BeansKit.class);

    private static final BeansKit instance = new BeansKit();

    private BeansKit(){}

    public static BeansKit getInstance(){
        return instance;
    }

    private ResourceLoader resourceLoader;

    public static <T> T getBeanFromContext(String nameOfBean, Class<T> requiredType,AbstractApplicationContext context){
        T obj = context.getBean(nameOfBean,requiredType);
        context.registerShutdownHook();
        return obj;
    }

    public static <T> T getBeanFromContext(String nameOfBean, Class<T> requiredType,ApplicationContext context ){
        // retrieve configured instance
        return context.getBean(nameOfBean, requiredType);
    }

    public static ApplicationContext tryGetContextSpring(String filePathXml, Class<?> thisClass) throws IOException {
        return loadApplicationContextSpring(thisClass,filePathXml);
    }

    private static ApplicationContext loadApplicationContextSpring(Class<?> thisClass, String... filePaths) {
        ApplicationContext context = new GenericApplicationContext();
        //This container loads the definitions of the beans from an XML file.
        // Here you do not need to provide the full path of the XML file but
        // you need to set CLASSPATH properly because this container will look
        // bean configuration XML file in CLASSPATH.
        //You can force with the fileSystem using "file:" instead of "classpath:".
        try {
            context = new ClassPathXmlApplicationContext(filePaths,true);
        } catch (Exception e0) {
            if (e0.getCause().getMessage().contains("has already been set")) {
                logger.warn(e0.getMessage() + "->" + e0.getCause());
            }
            try {
                context = new ClassPathXmlApplicationContext(filePaths, true);
            } catch (Exception e1) {
                if (thisClass != null) {
                    try {
                        context = new ClassPathXmlApplicationContext(filePaths, thisClass);
                    } catch (Exception e2) {

                        try {
                            //This container loads the definitions of the beans from an XML file.
                            // Here you need to provide the full path of the XML bean configuration file to the constructor.
                            //You can force with file: property to the class file.
                            List<String> files = new ArrayList<>();
                            for (String spath : filePaths) {
                                Path path = getResourceAsFile(spath, thisClass).toPath();
                                if (Files.exists(path) && path.toRealPath() != null) {
                                    files.add(path.toAbsolutePath().toString());
                                } else {
                                    logger.warn("The resource with path:" + path.toString()+" not exists");
                                }
                            }
                            if (!files.isEmpty()) {
                                context = new FileSystemXmlApplicationContext(files.toArray(new String[files.size()]), true);
                            } else {
                                logger.warn("The paths used are reference 0 resources return NULL value");
                                return null;
                            }
                        } catch (Exception e3) {
                            logger.error(e3.getMessage(), e3);
                        }
                    }
                } else {
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
        return context;
    }


    public static ApplicationContext tryGetContextSpring(String[] filesPathsXml,Class<?> thisClass) throws IOException {
        String[] paths = new String[filesPathsXml.length];
        int i = 0;
        for(String path : filesPathsXml){
            if(new File(path).exists()) {
                //String path = toStringUriWithPrefix(getResourceAsFile(spath, thisClass));
                paths[i] = path;
                i++;
            }
        }
        return loadApplicationContextSpring(thisClass,paths);
    }

    /*public static String getResourceAsString(String fileName,Class<?> thisClass) {
        String result;
        try {
            result = org.apache.commons.io.IOUtils.toString(thisClass.getClassLoader().getResourceAsStream(fileName));
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }*/

    public static File getResourceAsFile(String name,Class<?> thisClass) {
        try {
            //noinspection ConstantConditions
            return new File(thisClass.getClassLoader().getResource(name).getFile());
        }catch(NullPointerException e){
            logger.error(e.getMessage(), e);
            return new File("");
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,null,null).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder,Class<?> clazz) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,clazz,null).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static File getResourceSpringAsFile(String pathRelativeToFileOnResourceFolder,ClassLoader classLoader) {
        try {
            //noinspection ConstantConditions
            return getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,null,classLoader).getFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String getResourceSpringAsString(String pathRelativeToFileOnResourceFolder) {
        //noinspection ConstantConditions
        return readResource(getResourceSpringAsResource(pathRelativeToFileOnResourceFolder,null,null));
    }

    /**
     * Method to get a resource.
     * href: http://howtodoinjava.com/spring/spring-core/how-to-load-external-resources-files-into-spring-context/
     * @param fileNameOrUri the {@link Object} to convert to {@link Resource}
     *                      must be a {@link File},{@link URI},{@link URL},{@link Path},{@link String},{@link InputStream}
     * @param clazz the {@link Class} for reference to the resource folder.
     * @param classLoader the {@link ClassLoader} for load the resources.
     * @return the {@link Resource}.
     */
    private static Resource getResourceSpringAsResource(
            Object fileNameOrUri, @Nullable Class<?> clazz,@Nullable ClassLoader classLoader) {
        try {
            Resource yourfile;
            //if File
            if(fileNameOrUri instanceof File && ((File) fileNameOrUri).exists()){
                yourfile = new org.springframework.core.io.FileSystemResource(((File) fileNameOrUri));
            }
            //if URL
            else if(org.springframework.util.ResourceUtils.isUrl(String.valueOf(fileNameOrUri))  || fileNameOrUri instanceof URL) {
                if (fileNameOrUri instanceof URL) {
                    yourfile = new org.springframework.core.io.UrlResource((URL) fileNameOrUri);
                } else {
                    yourfile = new org.springframework.core.io.UrlResource(String.valueOf(fileNameOrUri));
                }
            //if Path or URI
            }else if(fileNameOrUri instanceof Path || fileNameOrUri instanceof URI) {
                if (fileNameOrUri instanceof Path && Files.exists((Path) fileNameOrUri)) {
                    yourfile = new org.springframework.core.io.PathResource((Path) fileNameOrUri);
                } else {
                    yourfile = new org.springframework.core.io.PathResource((URI) fileNameOrUri);
                }
          /*  }else if(fileNameOrUri instanceof Class){
                org.springframework.core.io.ClassRelativeResourceLoader relativeResourceLoader =
                        new org.springframework.core.io.ClassRelativeResourceLoader((Class<?>) fileNameOrUri);
                yourfile = relativeResourceLoader.getResource("")
                */
            //if InputStream
            }else if(fileNameOrUri instanceof InputStream){
                    yourfile = new org.springframework.core.io.InputStreamResource((InputStream) fileNameOrUri);
            }else if(fileNameOrUri instanceof byte[]){
                yourfile = new org.springframework.core.io.ByteArrayResource((byte[]) fileNameOrUri);
            //if String path toa file or String of a URI
            }else if(fileNameOrUri instanceof String){
                if (classLoader != null) {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri), classLoader);
                } else if (clazz != null) {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri), clazz);
                } else {
                    yourfile = new org.springframework.core.io.ClassPathResource(String.valueOf(fileNameOrUri));
                }
            }else{
                logger.error("Can't load the resource for the Object with Class:"+fileNameOrUri.getClass().getName());
                return null;
            }
            return yourfile;
        }catch(IOException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String readResource(Resource resource){
        try {
           /* org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.ClassPathResource(fileLocationInClasspath);*/
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public String readResource(String absolutePathToFile,ResourceLoader resourceLoader) throws IOException {
        //This line will be changed for all versions of other examples : "file:c:/temp/filesystemdata.txt"
        Resource banner = resourceLoader.getResource("file:"+absolutePathToFile);
        InputStream in = banner.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null)break;
            sb.append(line).append(System.getProperty("line.separator"));
        }
        reader.close();
        return sb.toString();
    }



    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Method to get a resource from relative path with spring.
     * @param location the {@link String} location of the file on the resource folders e.g. "location.txt".
     * @return the {@link Resource} of Spring core io.
     */
    public Resource getResource(String location){
        return resourceLoader.getResource(location);
    }


    /*
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/' and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     *
     * @param filePath string of the path to the file
     * @return path to the in uri formato with prefix file:///
     */
    /*private static String toStringUriWithPrefix(String filePath) {
        StringBuilder mapFileName = new StringBuilder(filePath);
        for (int i = 0; i < mapFileName.length(); i++) {
            if (mapFileName.charAt(i) == '\\')
                mapFileName.setCharAt(i, '/');
        }
        if (filePath.charAt(0) == '/') return "file://" + mapFileName.toString();
        else return "file:///" + mapFileName.toString();
    }*/

    /*
     * Method to convert a File to a URI with the prefix file://.
     *
     * @param file the File to convert.
     * @return the String URI with prefix.
     */
    /*public static String toStringUriWithPrefix(File file) {
        return toStringUriWithPrefix(file.getAbsolutePath());
    }*/
    
    //-----------------------------------------------------------------------------------------

    /*public static Collection<?> collect(Collection<?> collection, String propertyName) {
        return org.apache.commons.collections.CollectionUtils.collect(collection, new
                org.apache.commons.beanutils.BeanToPropertyValueTransformer(propertyName));
    }*/



  /*public static org.springframework.context.ApplicationContext createApplicationContext(String uri) throws MalformedURLException {
    org.springframework.core.io.Resource resource = getResourceSpringAsResource(uri,null,null);
    logger.debug("Using " + resource + " from " + uri);
    try {
      return new ResourceAdapterApplicationContext()ResourceXmlApplicationContext(resource) {
        @Override
        protected void initBeanDefinitionReader(org.springframework.beans.context.xml.XmlBeanDefinitionReader reader) {
          reader.setValidating(true);
        }
      };
    }
  }*/

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeforeInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("AfterInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }
}
