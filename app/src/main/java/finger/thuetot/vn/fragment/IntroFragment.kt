package finger.thuetot.vn.fragment



/**
 * Created by VinhNguyen on 11/6/2017.
 */
class IntroFragment {

//    private var fragment: Fragment = builder.fragment!!
//
//    @ColorRes
//    private var background: Int =  builder.background
//    @ColorRes
//    private var backgroundDark: Int = builder.backgroundDark
//    public var canGoForward: Boolean? = builder.canGoForward
//    private var canGoBackward: Boolean? =builder.canGoBackward
//    private var buttonCtaLabel: CharSequence? = builder.buttonCtaLabel
//    @StringRes
//    private var buttonCtaLabelRes =  builder.buttonCtaLabelRes
//    private var buttonCtaClickListener: View.OnClickListener? = builder.buttonCtaClickListener
//
//
//
//    override fun getFragment(): Fragment? {
//        return fragment
//    }
//
//    override fun setFragment(fragment: Fragment) {
//        this.fragment = fragment
//    }
//
//    override fun getBackground(): Int {
//        return background
//    }
//
//    override fun getBackgroundDark(): Int {
//        return backgroundDark
//    }
//    override fun HideGoForward(): Boolean {
//        return if (getFragment() is SlideFragment) {
//            (getFragment() as SlideFragment).
//        } else canGoForward!!
//    }
//    override fun canGoForward(): Boolean {
//        return if (getFragment() is SlideFragment) {
//            (getFragment() as SlideFragment).canGoForward()
//        } else canGoForward!!
//    }
//
//    override fun canGoBackward(): Boolean {
//        return if (getFragment() is SlideFragment) {
//            (getFragment() as SlideFragment).canGoBackward()
//        } else canGoBackward!!
//    }
//
//    override fun getButtonCtaClickListener(): View.OnClickListener? {
//        return if (getFragment() is ButtonCtaFragment) {
//            (getFragment() as ButtonCtaFragment).buttonCtaClickListener
//        } else buttonCtaClickListener
//    }
//
//    override fun getButtonCtaLabel(): CharSequence? {
//        return if (getFragment() is ButtonCtaFragment) {
//            (getFragment() as ButtonCtaFragment).buttonCtaLabel
//        } else buttonCtaLabel
//    }
//
//    override fun getButtonCtaLabelRes(): Int {
//        return if (getFragment() is ButtonCtaFragment) {
//            (getFragment() as ButtonCtaFragment).buttonCtaLabelRes
//        } else buttonCtaLabelRes
//    }
//
//    override fun equals(o: Any?): Boolean {
//        if (this === o) return true
//        if (o == null || javaClass != o.javaClass) return false
//
//        val that = o as IntroFragment?
//
//        if (background != that!!.background) return false
//        if (backgroundDark != that.backgroundDark) return false
//        if (canGoForward != that.canGoForward) return false
//        if (canGoBackward != that.canGoBackward!!) return false
//        if (buttonCtaLabelRes != that.buttonCtaLabelRes) return false
//        if (if (fragment != null) fragment != that.fragment else that.fragment != null)
//            return false
//        if (if (buttonCtaLabel != null) buttonCtaLabel != that.buttonCtaLabel else that.buttonCtaLabel != null)
//            return false
//        return if (buttonCtaClickListener != null) buttonCtaClickListener == that.buttonCtaClickListener else that.buttonCtaClickListener == null
//
//    }
//
//    override fun hashCode(): Int {
//        var result = if (fragment != null) fragment!!.hashCode() else 0
//        result = 31 * result + background
//        result = 31 * result + backgroundDark
//        result = 31 * result + if (canGoForward!!) 1 else 0
//        result = 31 * result + if (canGoBackward!!) 1 else 0
//        result = 31 * result + if (buttonCtaLabel != null) buttonCtaLabel!!.hashCode() else 0
//        result = 31 * result + buttonCtaLabelRes
//        result = 31 * result + if (buttonCtaClickListener != null) buttonCtaClickListener!!.hashCode() else 0
//        return result
//    }
//
//    class Builder {
//        var fragment: Fragment? = null
//        var img: Int? = null
//        var type: Int? = null
//        var title: String? = null
//        var detail: String? = null
//        @ColorRes
//        var background: Int = 0
//        @ColorRes
//        var backgroundDark = 0
//        var canGoForward = true
//        var canGoBackward = true
//        var buttonCtaLabel: CharSequence? = null
//        @StringRes
//        var buttonCtaLabelRes = 0
//        var buttonCtaClickListener: View.OnClickListener? = null
//
//        fun fragment(fragment: Fragment): Builder {
//            this.fragment = fragment
//            return this
//        }
//
//        fun fragment(@LayoutRes layoutRes: Int, @StyleRes themeRes: Int): Builder {
//            this.fragment = IntroFragmentFragment.newInstance(layoutRes, themeRes)
//            return this
//        }
//
//        fun fragment(@LayoutRes layoutRes: Int): Builder {
//            this.fragment = IntroFragmentFragment.newInstance(layoutRes)
//            return this
//        }
//
//        fun background(@ColorRes background: Int): Builder {
//            this.background = background
//            return this
//        }
//
//        fun backgroundDark(@ColorRes backgroundDark: Int): Builder {
//            this.backgroundDark = backgroundDark
//            return this
//        }
//
//        fun canGoForward(canGoForward: Boolean): Builder {
//            this.canGoForward = canGoForward
//            return this
//        }
//
//        fun canGoBackward(canGoBackward: Boolean): Builder {
//            this.canGoBackward = canGoBackward
//            return this
//        }
//
//        fun buttonCtaLabel(buttonCtaLabel: CharSequence): Builder {
//            this.buttonCtaLabel = buttonCtaLabel
//            this.buttonCtaLabelRes = 0
//            return this
//        }
//
//        fun buttonCtaLabelHtml(buttonCtaLabelHtml: String): Builder {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                this.buttonCtaLabel = Html.fromHtml(buttonCtaLabelHtml, Html.FROM_HTML_MODE_LEGACY)
//            } else {
//
//                this.buttonCtaLabel = Html.fromHtml(buttonCtaLabelHtml)
//            }
//            this.buttonCtaLabelRes = 0
//            return this
//        }
//
//        fun buttonCtaLabel(@StringRes buttonCtaLabelRes: Int): Builder {
//            this.buttonCtaLabelRes = buttonCtaLabelRes
//            this.buttonCtaLabel = null
//            return this
//        }
//
//        fun buttonCtaClickListener(buttonCtaClickListener: View.OnClickListener): Builder {
//            this.buttonCtaClickListener = buttonCtaClickListener
//            return this
//        }
//
//        fun build(): IntroFragment {
//            if (background == 0 || fragment == null)
//                throw IllegalArgumentException("You must set at least a fragment and background.")
//            return IntroFragment(this)
//        }
//    }
//
//
//    class IntroFragmentFragment : ParallaxFragment() {
//
//        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
//                                  savedInstanceState: Bundle?): View? {
//            val themeRes = arguments.getInt(ARGUMENT_THEME_RES)
//            val contextThemeWrapper: Context
//            if (themeRes != 0) {
//                contextThemeWrapper = ContextThemeWrapper(activity, themeRes)
//            } else {
//                contextThemeWrapper = activity
//            }
//            val localInflater = inflater!!.cloneInContext(contextThemeWrapper)
//
//            return localInflater.inflate(arguments.getInt(ARGUMENT_LAYOUT_RES), container, false)
//        }
//
//        companion object {
//            private val ARGUMENT_LAYOUT_RES = "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_LAYOUT_RES"
//            private val ARGUMENT_THEME_RES = "com.heinrichreimersoftware.materialintro.SimpleFragment.ARGUMENT_THEME_RES"
//
//            @JvmOverloads
//            fun newInstance(@LayoutRes layoutRes: Int, @StyleRes themeRes: Int = 0): IntroFragmentFragment {
//                val arguments = Bundle()
//                arguments.putInt(ARGUMENT_LAYOUT_RES, layoutRes)
//                arguments.putInt(ARGUMENT_THEME_RES, themeRes)
//
//                val fragment = IntroFragmentFragment()
//                fragment.arguments = arguments
//                return fragment
//            }
//        }
//    }
}
