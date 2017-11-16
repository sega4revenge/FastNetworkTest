package finger.thuetot.vn.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detailproductneed.*
import kotlinx.android.synthetic.main.toolbar_twoline.*
import finger.thuetot.vn.R
import finger.thuetot.vn.model.Product
import finger.thuetot.vn.model.User

/**
 * Created by VinhNguyen on 9/1/2017.
 */
class DetailNeedProductActivity : AppCompatActivity() {
    var product: Product? = null
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailproductneed)

        var intent = intent
        product = intent.getParcelableExtra("product_id")
        user = product?.user
        setDataToView(product,user)

    }
    private fun setDataToView(mProduct: Product?,user: User?) {
        toolbar_title.text = mProduct?.productname
        toolbar_subtitle.text = mProduct?.created_at
        detailproduct.text = mProduct?.description
        val options = RequestOptions()
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .error(R.drawable.img_error)
                .priority(Priority.HIGH)
        Glide.with(this)
                .load(user?.photoprofile)
                .thumbnail(0.1f)
                .apply(options)
                .into(avata)
        nameUser.text = user?.name
        address.text = mProduct?.location!!.address
        joindate.text = user?.created_at
    }
}