
def main(args):
    import tensorflow as tf
    import time

    start = time.time()
    with tf.device('/gpu:0'):
        number = float(args.get("constant"))
        a = tf.constant([number, 2.0, 3.0, 4.0, 5.0, 6.0], shape=[2, 3], name='a')
        b = tf.constant([1.0, 2.0, 3.0, 4.0, 5.0, 6.0], shape=[3, 2], name='b')
        c = tf.matmul(a, b)

        with tf.Session() as sess:
            vari = sess.run(c)
            end = time.time()
            return {"result": str(vari), "duration": str(end-start)}
