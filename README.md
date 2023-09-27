# SpeechRecognitionInterface

#### 介绍
针对语音识别模型whisper的SpringBoot的一个接口整合

#### 软件架构
软件架构说明


#### 安装教程

##### 安装或升级python 3.9.1

```
#如果升级安装过程有问题，可以执行下面命令
yum update -y

yum -y groupinstall "Development tools"
 
yum install openssl-devel bzip2-devel expat-devel gdbm-devel readline-devel sqlite-devel psmisc libffi-devel
```

```
mkdir /opt/python3

cd /opt/python3

#下载
wget https://www.python.org/ftp/python/3.9.1/Python-3.9.1.tgz

#解压
tar -xzf Python-3.9.1.tgz

#编译安装
cd Python-3.9.1

./configure --prefix=/usr/local/python3

make && make install

#修改系统路径和指向
#先移动或者备份
 mv /usr/bin/python /usr/bin/python_old
 mv /usr/bin/pip /usr/bin/pip_old
 
#配置系统连接和路径指针
ln -s /usr/local/python3/bin/python3.9 /usr/bin/python
ln -s /usr/local/python3/bin/pip3.9 /usr/bin/pip

#检查版本
python -V

#修改yum使用的python版本 yum使用python3会出错
#这两个文件第一行最后python改为python2即可
vi /usr/bin/yum
vi /usr/libexec/urlgrabber-ext-down



#若有升级pip的需要
python -m pip install --upgrade pip -i https://pypi.douban.com/simple
```

##### 安装pytorch

```
官网 查找版本  gpu更适合深度学习
https://pytorch.org/get-started/locally/
#cpu
pip install torch==2.0.1 torchvision==0.15.2 torchaudio==2.0.2 --index-url https://download.pytorch.org/whl/cpu


#win10 
#英伟达显卡安装cdna
https://www.ngui.cc/el/3180290.html?action=onClick

#cuda安装教程
https://blog.csdn.net/m0_45447650/article/details/123704930
#去官网找显卡适合的对应版本安装torch


#查看
nvcc --version
set cuda
#测试是否成功 返回为true为成功
python
import torch
torch.cuda.is_available()

#显卡用于深度学习的优化   我全部选择了11.8版本
https://developer.nvidia.com/cuda-toolkit-archive
```



##### 安装whisper

```text
#官方安装
pip install -U openai-whisper
#或者，以下命令将从此存储库中提取并安装最新的提交及其 Python 依赖项：
pip install git+https://github.com/openai/whisper.git 
#要将软件包更新到此存储库的最新版本，请运行：
pip install --upgrade --no-deps --force-reinstall git+https://github.com/openai/whisper.git

#或利用清华或豆瓣源安装
pip install -i https://pypi.doubanio.com/simple/ openai-whisper 
pip install -i https://pypi.tuna.tsinghua.edu.cn/simple +包名

#如果安装成功但提示命令不存在则添加环境变量
export PATH="/usr/local/python3/bin:$PATH"

```

##### **安装ffmpeg**  编译安装

```
windows环境  官网 下载http://ffmpeg.org/
到FFmpeg官网（ffmpeg.org）的“Download”页面选择Windows图标，选择“Windows build from gyan.dev”，然后在“release builds”下选择“ffmpeg-release-full-shared.7z”。

只需要解压Bin文件夹内的文件，解压出Bin文件夹下的EXE和DLL后，您需要把它们所在的路径加入PATH
```

linux:

```
git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg


#然后进入ffmpeg文件夹，依次执行下列语句，当然连起来也可以：
cd ffmpeg
./configure
make
make install

#时间较长，不出意外会正常安装好。
#但是因为configure时候没有指定路径，所以直接ffmpeg会提示找不到。
#所以要将编译好的ffmpeg复制到bin目录即可：

cp ffmpeg /usr/bin/ffmpeg

#然后检查版本

ffmpeg -version

#可能编译时会出错 nasm/yasm not found or too old. Use --disable-x86asm for a crippled build.
#yasm是汇编编译器，ffmpeg为了提高效率使用了汇编指令，如MMX和SSE等。所以系统中未安装yasm时，就会报上面错误。
#则需要安装
wget http://www.tortall.net/projects/yasm/releases/yasm-1.3.0.tar.gz

tar -zxvf yasm-1.3.0.tar.gz 

cd yasm-1.3.0

./configure
make
make install

```

##### 使用说明

```
https://blog.csdn.net/qq_43907505/article/details/130590052
```
