package posy_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Post_main {
	static Scanner sc = new Scanner(System.in);
	private static Map<String, Integer> menu = new HashMap<String, Integer>();
	
	public static void init(){//Pos기를 키면 초기화를 한다. menu.txt에서 메뉴를 입력받아 저장하고 매출액을 0으로 초기화시킨다.
		int count = 0;
		String tmp1 = "";//메뉴
		int tmp2;//가격
		try{
            File file = new File("./menu.txt");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
                if(count %2 == 0){
                	tmp1 = line;
                }else{
                	tmp2 = Integer.parseInt(line);
                	menu.put(tmp1, tmp2);
                }
            	++count;
            }            
            bufReader.close();
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }
		System.out.println("시스템 가동 및 초기화 완료\n");
		String init_log = "";
		init_log = "(init)시스템 가동 및 초기화 완료\n";
		System_access.admin_log(init_log);
	}
	//초기설정
	
	public static void select_menu(Customer[] people){
		String hm;
        int num = 0;
        int price = 0;
        String total_sale_log = "";
        while(true){
        	//몇번 손님인지 확인해야함 
	        System.out.println("메뉴선택\norder   more_order   calculate   add_menu   sub_menu   total_sale   menu_find   exit");
	        hm = sc.nextLine();
	        if(hm.equals("order")){
	        	System.out.println("몇번손님(0~9)");
	        	num = sc.nextInt();
	        	sc.nextLine();
	        	people[num] = new Customer();
	        	people[num].set_table(num);
	        	Sale.order(people[num], menu);
	        	people[num].countdown();//order은 첫 주문이므로 첫 주문과 동시에 카운트다운이 이뤄져야
	        }else if(hm.equals("more_order")){
	        	System.out.println("몇번손님(0~9)");
	        	num = sc.nextInt();
	        	sc.nextLine();
	        	Sale.more_order(people[num], menu);
	        }else if(hm.equals("calculate")){
	        	System.out.println("몇번손님(0~9)");
	        	num = sc.nextInt();
	        	sc.nextLine();
	        	System.out.print("고객이 지불한 금액 : ");
	        	price = sc.nextInt();
	        	sc.nextLine();
	        	Sale.calculate(people[num], price);//sales를 클래스 객체로 바꿔야함  고객.sales로
	        	people[num].set_count_minute(-1);//계산한 후에 고객 객체의 count_minute를 0으로 만들어 이용시간이 종료되도록 한다.
	        	//객체제거도 해야할거 같은데
	        }else if(hm.equals("add_menu")){
	        	menu_access.add_menu(menu);
	        }else if(hm.equals("sub_menu")){
	        	menu_access.sub_menu(menu);
	        }else if(hm.equals("menu_find")){
	        	menu_access.menu_find();
	        }else if(hm.equals("total_sale")){
	        	System.out.println("총 매출 : " + Sale.total_sales + "원");
	        	total_sale_log = "(total_sale)총 매출 : " + Sale.total_sales + "원\n";
	        	System_access.admin_log(total_sale_log);
	        }else if(hm.equals("exit")){
	        	break;
	        }else{
	        	System.out.println("오타확인 다시 입력해주십시오");
	        }
        }
        System.out.println("시스템이 종료되었습니다.");
        String logout = "";
        logout = "시스템이 종료되었습니다.\n";
        System_access.admin_log(logout);
	}
	
	
	public static void main(String[] args){
        init();
		Customer[] people = new Customer[10];
		
		for(int i = 0; i<10; i++){
			people[i] = new Customer();
			people[i].set_table(i);
		}//객체들 초기화 - 초기화 안해서 널포인트 에러 떴었음 신기하네
        
        select_menu(people);
	}
}

class Customer{
	private int sales;
	private int count_minute;
	private int table_set;
	
	public Customer(){
		sales = 0;
		count_minute = 40;
		table_set = 0;
	}
	public int get_sales(){
		return sales;
	}
	public void set_sales(int sale){
		sales = sale;
	}
	public int get_table(){
		return table_set;
	}
	public void set_table(int table){
		table_set = table;
	}
	public int get_count_minute(){
		return count_minute;
	}
	public void set_count_minute(int count_minute){
		this.count_minute = count_minute;
	}
	
	//결제시 카운트가 종료되어야하는데 m_timer.cancel()할경우 따 꺼진다 그러니 타이머 배열을 찾든 타이머 10개를 만들어서 따로 운용해줘야하는 상황
	public void countdown(){
		Timer m_timer[] = new Timer[10];
		for(int i = 0; i < 10; i++){
			m_timer[i] = new Timer();
		}
		TimerTask m_task = new TimerTask(){
			public void run(){//나중에 고객 클래스에서 count_minute인자로 받아서 써야할듯...?
				if(count_minute >= 0){
					if(count_minute % 10 ==0 && count_minute != 0){//매초마다 초기화 하지만 표시는 10초에 한번씩 갱신, 그리고 calculate할 경우 이용시간 0이 바로 갱신될 수 있도록
					System.out.println(table_set +"번 고객의 이용시간이"+count_minute + "분 남았습니다.");
					}
					count_minute = count_minute-1;
					if(count_minute == 0){
						System.out.println(table_set +"번 고객의 이용시간이 종료되었습니다.");
					}
				}else{
					m_timer[table_set].cancel();
				}
			}
		};
		m_timer[table_set].schedule(m_task,0,1000);
	}	
}


class Sale {
	static int total_sales;
	static String order_menu;
	static Scanner sc = new Scanner(System.in);
	
	public Sale(){
		total_sales = 0;
	}
	
	public static void order(Customer people, Map<String, Integer> menu){
		String order_log = "";
		while(true){
        	System.out.println("무엇을 주문하시겠습니까");
        	order_menu = sc.nextLine();
        
        	if(order_menu.equals("exit")){
        		break;
        	}else if(menu.containsKey(order_menu) == false){
        		System.out.println("없는 메뉴입니다 다시 시도하십시오\n");
        		continue;
        	}else{
        		System_access.output(menu, order_menu);
        		people.set_sales(people.get_sales() + menu.get(order_menu));
        		order_log = "(order)"+people.get_table() + "번 고객이 " + order_menu + "를 주문하였습니다.(첫주문)\n";
        		System_access.admin_log(order_log);
        	}
        }
		System.out.println("총 금액 : " + people.get_sales() +"원\n");//sales를 고객 객체마다 1개씩
		//카운트다운 넣어야합니다. countdown(people); 객체로 받은 후
	}
	//처음주문
/*	
	public static void countdown(){//고객 클래스를 인자로 받아야함
		m_timer.schedule(m_task,0,1000);
	}
	//처음주문 후 시간 흘러가야함
*/
	public static void more_order(Customer people, Map<String, Integer> menu){
		String more_order_log = "";
		System.out.println("추가주문 입니다.");
		while(true){
        	System.out.println("무엇을 주문하시겠습니까");
        	order_menu = sc.nextLine();
        
        	if(order_menu.equals("exit")){
        		break;
        	}else if(menu.containsKey(order_menu) == false){
        		System.out.println("없는 메뉴입니다 다시 시도하십시오");
        		continue;
        	}else{
        		System_access.output(menu, order_menu);
        		people.set_sales(people.get_sales() + menu.get(order_menu));
        		more_order_log = "(more_order)"+people.get_table() + "번 고객이 " + order_menu + "를 주문하였습니다.\n";
        		System_access.admin_log(more_order_log);
        	}
        }
		System.out.println("총 금액 : " + people.get_sales()+"원\n");//sales를 고객 객체마다 1개씩
	}
	
	public static void calculate(Customer people, int money){
		if(people.get_sales() > money){
			System.out.println("금액이 모자랍니다.");//돈이 모자랄때도 생각해야함
		}else{
			System.out.println("고객에게 " + money + "원을 받고 " + (money-people.get_sales()) + "원을 거슬러주었습니다.");
		}
		total_sales = total_sales + people.get_sales();
		
		String calculate_log = "";
		calculate_log = "(calculate)고객에게 " + money + "원을 받고 " + (money-people.get_sales()) + "원을 거슬러주었습니다.\n";
		System_access.admin_log(calculate_log);
		//계산하면 고객의 sales를 0으로 초기화해주고, countdown도 120으로 초기화해준다.
	}
	//계산
	
	
}


class System_access{
	public static void output(Map<String, Integer> menu, String a){
		System.out.println("고객님께서 " + a + "를 " + menu.get(a) + "원에 주문하셨습니다.\n");
	}
	
	public static void admin_log(String a){
		File file = new File("./admin_log.txt");
		FileWriter writer = null;
		try{
			writer = new FileWriter(file, true);
			writer.write(a);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}

class menu_access{
	static String add_menu;
	static String sub_menu;
	static int menu_price;
	static Scanner sc = new Scanner(System.in);
	
	public static void add_menu(Map<String, Integer> menu){
		System.out.println("어떤 메뉴를 추가하시겠습니까?");
		add_menu = sc.nextLine();
		System.out.println("가격은 얼마입니까?");
		menu_price = sc.nextInt();
		menu.put(add_menu, menu_price);
		sc.nextLine();
		
		File file = new File("./menu.txt");
		FileWriter writer = null;
		try{
			writer = new FileWriter(file, true);
			writer.write("\n" + add_menu + "\n" + menu_price);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(writer != null){
					writer.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		String add_menu_log = "";
		add_menu_log = "(add_menu)"+add_menu + "를 메뉴에 추가하셨습니다.\n ";
		System_access.admin_log(add_menu_log);
	}
	//메뉴추가
	
	public static void sub_menu(Map<String, Integer> menu){//txt파일에서도 제거해야하는데 못함
		System.out.println("어떤 메뉴를 제거하시겠습니까?");
		sub_menu = sc.nextLine();
		menu.remove(sub_menu);
		String sub_menu_log = "";
		sub_menu_log ="(sub_menu)"+ sub_menu + "를 메뉴에서 제거하셨습니다.\n ";
		System_access.admin_log(sub_menu_log);
	}
	//메뉴제거
	
	public static void menu_find(){
		int count = 0;
		try{
            File file = new File("./menu.txt");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
                if(count %2 == 0){
                	System.out.print(line + " : ");
                }else{
                	System.out.println(line);
                }
            	++count;
            }            
            bufReader.close();
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }
	}
}


















