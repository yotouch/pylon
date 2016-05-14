package weixin.popular.bean.paymch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchOrderInfoResult extends MchBase{

	@XmlElement
	private String trade_state;

	@XmlElement
	private String device_info;

	@XmlElement
	private String openid;

	@XmlElement
	private String is_subscribe;

	@XmlElement
	private String trade_type;

	@XmlElement
	private String bank_type;

	@XmlElement
	private Integer total_fee;

	@XmlElement
	private String fee_type;

	@XmlElement
	private Integer cash_fee;

	@XmlElement
	private String cash_fee_type;

	@XmlElement
	private Integer coupon_fee;

	@XmlElement
	private Integer coupon_count;

	@XmlElement
	private String transaction_id;

	@XmlElement
	private String out_trade_no;

	@XmlElement
	private String attach;

	@XmlElement
	private String time_end;

	@XmlElement
	private String trade_state_desc;
	
	@XmlElement
    private String err_code;
	
	@XmlElement
    private String return_code;
	
	@XmlElement
    private String return_msg;
	
	

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public Integer getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(Integer total_fee) {
		this.total_fee = total_fee;
	}

	public Integer getCoupon_fee() {
		return coupon_fee;
	}

	public void setCoupon_fee(Integer coupon_fee) {
		this.coupon_fee = coupon_fee;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public Integer getCash_fee() {
		return cash_fee;
	}

	public void setCash_fee(Integer cash_fee) {
		this.cash_fee = cash_fee;
	}

	public String getCash_fee_type() {
		return cash_fee_type;
	}

	public void setCash_fee_type(String cash_fee_type) {
		this.cash_fee_type = cash_fee_type;
	}

	public Integer getCoupon_count() {
		return coupon_count;
	}

	public void setCoupon_count(Integer coupon_count) {
		this.coupon_count = coupon_count;
	}

	public String getTrade_state_desc() {
		return trade_state_desc;
	}

	public void setTrade_state_desc(String trade_state_desc) {
		this.trade_state_desc = trade_state_desc;
	}
	

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    @Override
    public String toString() {
        return "MchOrderInfoResult [trade_state=" + trade_state + ", device_info=" + device_info + ", openid=" + openid
                + ", is_subscribe=" + is_subscribe + ", trade_type=" + trade_type + ", bank_type=" + bank_type
                + ", total_fee=" + total_fee + ", fee_type=" + fee_type + ", cash_fee=" + cash_fee + ", cash_fee_type="
                + cash_fee_type + ", coupon_fee=" + coupon_fee + ", coupon_count=" + coupon_count + ", transaction_id="
                + transaction_id + ", out_trade_no=" + out_trade_no + ", attach=" + attach + ", time_end=" + time_end
                + ", trade_state_desc=" + trade_state_desc + ", err_code=" + err_code + ", return_code=" + return_code
                + ", return_msg=" + return_msg + "]";
    }




}
