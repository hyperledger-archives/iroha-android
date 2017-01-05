LIBDIR := lib

all: $(LIBDIR) $(LIBDIR)/libed25519.a $(LIBDIR)/libed25519.so 

CC := gcc
CFLAGS := -O2 -pipe
CFLAGS += -Wall -pedantic
CFLAGS += -target mips64el-none-linux-android

LDFLAGS += -Led25519
LIBDIR := lib

$(LIBDIR):
	mkdir -p $(LIBDIR)

SRCS = $(wildcard src/*.c)
OBJS = $(SRCS:.c=.o)

%.o: %.c
	$(CC) -c -o $@ $< $(CFLAGS)

$(LIBDIR)/libed25519.a: $(OBJS)
	ls $(OBJS)
	ar rcs $@ $^

$(LIBDIR)/libed25519.so: $(OBJS)
	$(CC) -shared $(OBJS) -o $@

.PHONY: test
test: $(LIB)
	$(CC) -o $@ test.c $(LDFLAGS) -L$(LIBDIR) -led25519

.PHONY: clean
clean:
	rm -fr $(LIBDIR)  test test.o $(OBJS)


