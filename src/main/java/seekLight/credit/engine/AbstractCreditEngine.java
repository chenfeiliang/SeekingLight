package seekLight.credit.engine;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.credit.excetion.FlowAsyncInterrupterException;
import seekLight.credit.excetion.FlowInterrupterException;
import seekLight.credit.flow.Flow;
import seekLight.credit.flow.ModelParamCacheable;
import seekLight.credit.helper.ParamKeysInitHelper;
import seekLight.credit.lock.MethodLock;
import seekLight.credit.plugin.Plugin;
import seekLight.credit.plugin.impl.*;
import seekLight.credit.runner.ContextRunner;
import seekLight.credit.service.FlowService;

import java.util.*;
@Component
public abstract class AbstractCreditEngine <S extends FlowService<F>,F extends Flow> implements CreditEngine<F>{

    public static final String END_STEP = "07";
    @Autowired
    private  List<Plugin> plugins;
    @Autowired
    protected ParamKeysInitHelper paramKeysInitHelper;
    @Autowired
    protected S flowService ;

    @MethodLock(key = "#flow.busiSno")
    @Override
    public void doFlow(F flow) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Runnable finalRunner = null;
                if(flow instanceof ModelParamCacheable){
                    paramKeysInitHelper.setParamKeysCache((ModelParamCacheable & Flow)flow);
                }
                String curStep = getStep(flow);
                try {
                    check(flow);
                    while(StringUtils.isNotEmpty(curStep)&&
                            (!StringUtils.equals(curStep,END_STEP))){
                        Plugin plugin = getPlugin(curStep);
                        plugin.run(flow);
                        curStep =
                                flowService.updateStepToNext(flow);
                    }
                }catch (FlowAsyncInterrupterException e){
                    finalRunner = e.getRunnable();
                    System.out.println("异步请求并中断等待");
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    flowService.saveOrUpdateFlow(flow);
                    if(Objects.nonNull(finalRunner)){
                        finalRunner.run();
                    }
                }

            }
        };
        runByContext(runnable,flow);
    }

    private void check(F flow){
        List<String> routes =
                Arrays.asList(flow.getRoute().split(","));
        if(routes.size()!=new HashSet<>(routes).size()){
            //重复路由
            throw new FlowInterrupterException("重复路由");
        }
    }

    private Plugin getPlugin(String pluginName){
        for(Plugin plugin :plugins){
            if(StringUtils.equals(plugin.getName(),
                    pluginName)){
                return plugin;
            }
        }
        throw new FlowInterrupterException("未知step: "+pluginName);
    }

    private void runByContext(Runnable runnable,Flow flow){
        new ContextRunner(runnable,flow.getBusiSno()).run();
    }

    private String getStep(Flow flow){
        String curStep = flow.getStep();
        if(StringUtils.isEmpty(curStep)){
            List<String> routes =
                    Arrays.asList(flow.getRoute().split(","));
            if(!routes.isEmpty()){
                curStep = routes.get(0);
                flow.setTransTime(new Date());
                flow.setStep(curStep);
            }
        }
        return curStep;
    }
}
