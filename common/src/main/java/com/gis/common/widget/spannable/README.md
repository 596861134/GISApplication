```
    演示效果：https://raw.githubusercontent.com/liangjingkanji/spannable/master/preview_img.png
    
    用法示例：
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 替换Span
        binding.tv.movementMethod = ClickableMovementMethod.getInstance()
        binding.tv.text = "隐私权政策 | 许可 | 品牌指南".replaceSpan("隐私权政策") {
            URLSpan("https://github.com/") // 仅替换效果
        }

        // 自动设置网址
        binding.tv1.movementMethod = LinkMovementMethod()
        binding.tv1.text = "打开官网: https://github.com/" // 地址/邮箱/手机号码等匹配可以不使用Span, 可以在xml中指定autoLink属性, 会有点击背景色

        // 使用正则匹配
        binding.tv2.movementMethod = ClickableMovementMethod.getInstance() // 保证没有点击背景色
        binding.tv2.text = "我们可以艾特用户@刘强东 或者创建#热门标签"
            .replaceSpan("@[^@]+?(?=\\s|\$)".toRegex()) { matchResult ->
                HighlightSpan("#ed6a2c") {
                    Toast.makeText(this@MainActivity, "点击用户 ${matchResult.value}", Toast.LENGTH_SHORT).show()
                }
            }.replaceSpan("#[^@]+?(?=\\s|\$)".toRegex()) { matchResult ->
                HighlightSpan("#4a70d2", Typeface.defaultFromStyle(Typeface.BOLD)) {
                    Toast.makeText(this@MainActivity, "点击标签 ${matchResult.value}", Toast.LENGTH_SHORT).show()
                }
            }

        // 仅替换第一个匹配项
        binding.tv3.text = "隐私权政策 | 隐私权政策 | 品牌指南".replaceSpanFirst("隐私权政策") {
            SpannableString("用户协议").setSpan(URLSpan("https://github.com/")) // 替换文字和效果
        }

        // 添加一个字符串+Span, 注意括号保证函数执行优先级
        binding.tv4.text = ("隐私权政策 | 许可 | 品牌指南" + " | ").addSpan("官网", listOf(ColorSpan(Color.BLUE), StyleSpan(Typeface.BOLD)))

        // 通过拼接方式展示价格
        binding.tv5.text = "¥".setSpan(ColorSpan("#ed6a2c"))
            .addSpan("39.9", arrayOf(ColorSpan("#ed6a2c"), AbsoluteSizeSpan(18, true)))
            .addSpan(" 1000+ 人付款")
            .addSpan("image", CenterImageSpan(this, R.drawable.ic_touch).setDrawableSize(20.dp).setMarginHorizontal(4.dp)).addSpan("点击付款")

        // 通过替换方式展示价格
        /*binding.tv6.text = "头像".setSpan(
            GlideImageSpan(binding.tv6, "https://avatars.githubusercontent.com/u/21078112?v=4")
                .setRequestOption(RequestOptions.circleCropTransform()) // 圆形裁剪图片
                .setAlign(GlideImageSpan.Align.BOTTOM)
                .setDrawableSize(50.dp)
        ).addSpan("¥39.9 1000+ 人付款 ")
            .replaceSpan("¥[\\d\\.]+".toRegex()) { // 匹配价格颜色(包含货币符号)
                ColorSpan("#479fd1")
            }.replaceSpanFirst("[\\d\\.]+".toRegex()) { // 匹配价格字号
                AbsoluteSizeSpan(18, true)
            }*/
        binding.tv6.text = "头像".setSpan(
            GlideImageSpan(binding.tv6, "https://img.tukuppt.com/png_preview/00/43/99/f2NMN17H0m.jpg%21/fw/780")
//                .setRequestOption(RequestOptions.circleCropTransform()) // 圆形裁剪图片
                .setAlign(GlideImageSpan.Align.CENTER)
                .setMarginHorizontal(0, 5.dp)
                .setDrawableSize(0, -1))
            .addSpan("旺旺 神萃柠檬茶 原味350ml*24*旺旺 神萃柠檬茶 原味350ml*24*旺旺 神萃柠檬茶 原味350ml*24*旺旺 神萃柠檬茶 原味350ml*24*")


        // GIF图文混排
        binding.tv7.text = "播放GIF动画[晕]表情".replaceSpan("[晕]") {
            GlideImageSpan(binding.tv7, R.drawable.ic_gif)
                .setRequestOption(RequestOptions.centerCropTransform())
                .setDrawableSize(50.dp)
        }

        binding.tv8.text = "使用shape".addSpan(
            "自适应标签",
            listOf(
                CenterImageSpan(this, R.drawable.bg_label)
                    .setDrawableSize(-1)
                    .setPaddingHorizontal(6.dp)
                    .setTextSize(16.dp) // 区别于AbsoluteSizeSpan是完全居中对齐行
                    .setTextVisibility(),
                ColorSpan(Color.WHITE),
                StyleSpan(Typeface.BOLD)
            )
        ).addSpan("构建自适应文字宽高标签")

        binding.tv9.text = "自适应点九图片".setSpan(
            listOf(
                CenterImageSpan(this, R.drawable.bg_date_label)
                    .setDrawableSize(-1)
                    .setTextVisibility(),
                ColorSpan(Color.BLACK),
                AbsoluteSizeSpan(16, true),
                StyleSpan(Typeface.BOLD)
            )
        ) addSpan "适用于可伸展PNG"

        binding.tv10.text = "塞尔达公主是任天堂游戏塞尔达传说系列的主要角色。她由宫本茂创造，最早于1986年游戏《塞尔达传说》中登场%s 。 根据宫本茂所述，塞尔达的名字受到美国小说家泽尔达·菲茨杰拉德所影响。宫本茂解释到：“菲茨杰拉德是一个著名的且漂亮的女性，我喜欢她名字读出的声音”。 塞尔达公主几乎都在全部塞尔达传说作品中出现。".replaceSpan("%s") {
            GlideImageSpan(binding.tv10, R.drawable.ic_zelda)
                .setRequestOption(RequestOptions.centerCropTransform())
                .setAlign(GlideImageSpan.Align.BOTTOM)
                .setDrawableSize(100.dp)
        }
    }

```

<p align="center"> <strong>Spannable和创建字符串一样简单</strong> </p>

<br>
<p align="center">
<img src="https://raw.githubusercontent.com/liangjingkanji/spannable/master/preview_img.png" width="450"/><br>
<img src="https://user-images.githubusercontent.com/21078112/184396518-4022db12-0fa9-48a0-97c1-22960db9362b.png" width="350"/>
</p>

<p align="center">
<a href="https://jitpack.io/#liangjingkanji/spannable"><img src="https://jitpack.io/v/liangjingkanji/spannable.svg"/></a>
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<img src="https://img.shields.io/badge/license-MIT-blue"/>
<img src="https://raw.githubusercontent.com/liangjingkanji/liangjingkanji/master/img/group.svg"/>
</p>

<p align="center">
<a href="https://github.com/liangjingkanji/spannable/releases/latest/download/spannale-sample.apk">下载体验</a>
</p>

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/21078112/163671712-0a8644b3-8875-489e-a1e5-f8f3215ff4fc.png" width="550"/>
</p>
<br>

让Spannable和字符串一样易用, 快速构建常见的富文本/图文混排/表情包/图标

1. 全网第一个实现**正则替换**
2. 全网第一个同时实现加载`Drawable/网络图片/Shape/点九图`格式

## 特点

- [x] 低学习成本(仅四个函数)
- [x] 首个支持替换/正则/反向捕获组Span的库
- [x] 全部使用CharSequence接口, 使用起来和字符串没有区别
- [x] 没有自定义控件/没有多余函数
- [x] 快速实现图文混排/富文本/自定义表情包/图标
- [x] 输入框富文本/表情包, 可监听剪贴板粘贴/手动输入文本渲染

## 函数

使用的函数非常简单

| 函数             | 介绍                                    |
| ---------------- | --------------------------------------- |
| setSpan          | 设置Span                                |
| addSpan          | 添加/插入Span或字符串                   |
| replaceSpan      | 替换/正则匹配Span或字符串               |
| replaceSpanFirst/replaceSpanLast | 替换第一个/最后一个匹配的Span或字符串 |

## 文本效果-Span
本框架会收集一些常用的Span效果实现, **欢迎贡献代码**

| Span | 描述 |
|-|-|
| CenterImageSpan | 垂直对齐方式/图片宽高/固定图片比例/显示文字/自适应文字宽高/Shape/.9图 |
| GlideImageSpan | 网络图片/GIF动画/垂直对齐方式/图片宽高/固定图片比例/显示文字/自适应文字宽高, Require [Glide](https://github.com/bumptech/glide) |
| MarginSpan | 文字间距 |
| ColorSpan | 快速创建文字颜色 |
| HighlightSpan | 创建字体颜色/字体样式/可点击效果 |
| ClickableMovementMethod | 等效LinkMovementMethod, 但没有点击背景色 |



本工具将保持简单和扩展性, 如果你想使用dsl构建span可以使用[SpannableX](https://github.com/TxcA/SpannableX)

## 安装

在项目根目录的 settings.gradle 添加仓库

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

然后在 module 的 build.gradle 添加依赖框架

```groovy
implementation 'com.github.liangjingkanji:spannable:1.2.7'
```



## License

```
MIT License

Copyright (c) 2023 劉強東

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
