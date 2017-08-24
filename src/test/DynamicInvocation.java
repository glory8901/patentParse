package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicInvocation {
	public static void main(String[] a) throws Exception {
		String Configure = "Class:com.bjsxt.service.school,Method:getStudentInfo,args:Tom,argsType:java.lang.String";// 格式固定
																														// 可以用正则表达式提取
		String[] split = { ":", "," };// 格式为 name:value, 所以分隔符为 ： ，
		parseData p = new parseData(Configure);// 实现方式为正则表达式提取需要的字符串
		// (1) 获取类名 方法名 参数 参数类型信息
		String className = p.getInfo("Class", split);
		String MethodName = p.getInfo("Method", split);
		String arg = p.getInfo("args", split);
		Object[] args = { arg };
		String argsType = p.getInfo("argsType", split);
		
		// (2) 创建未知对象实例
		Object s = Class.forName(className).newInstance();// 注意我们目前创建的对象并不知道其类型
		
		// (3)方法调用
		// 3.1仅通过方法名查找查找方法并调用 缺点：有可能有方法是重载的
		DynamicInvocation inv = new DynamicInvocation();
		inv.invokeMethodGernaral(s, MethodName, args);
		// 3.2通过方法名 和参数 查找方法并调用
		Class cls = Class.forName(argsType);
		System.out.println(cls.getName());
		Class[] clz = { cls };
		inv.invokeMethod(s, MethodName, clz, args);
		// （4）动态强制类型转换
		Class intClass = Class.forName("java.lang.Integer");
		System.out.println(Integer.class);

	}

	public Object invokeMethodGernaral(Object owner, String methodName, Object[] args)// 只通过方法的名字进行查找
																						// 并调用
	{
		// a.先获取对象所属的类
		Class ownerClass = owner.getClass();
		Method method = null;
		Object result = null;
		// b.获取需要调用的方法
		for (Method m : ownerClass.getDeclaredMethods()) {
			if (m.getName().equalsIgnoreCase(methodName)) {
				method = m;
				break;
			}
		}
		try {
			// c.调用该方法
			result = method.invoke(owner, args);// 调用方法
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Object invokeMethod(Object owner, String methodName, Class[] clz, Object[] args) throws Exception {
		// a.得到对象所属类
		Class ownerClass = owner.getClass();
		// b.根据方法名称和参数名称 获取该类的某个方法
		Method method = ownerClass.getMethod(methodName, clz);// 第二个参数是通过类型来获取
																// 有个缺点就是参数类型必须要填写
		// c.执行某个对象的方法
		Object result = method.invoke(owner, args); // 必须要有类对象才可以调用
		// d.输出结果信息
		System.out.println("结果返回值：" + result);
		return result;
	}
}
