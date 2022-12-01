import matplotlib.pyplot as plt
import sys, getopt

def make_RPC_picture(file_name):
    f = open(file_name, "r")
    lines = f.readlines()
    f.close()

    precision_lines = lines[10:21]
    precision = []
    recall = [0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
    for i in precision_lines:
        precision.append(float(i.split()[-1]))

    plt.plot(recall, precision, label=file_name)
    plt.title('Interpolated Precision-Recall Curve')
    plt.xlabel('Recall')
    plt.ylabel('Precision')

def make_p_at_picture(file_name):
    f = open(file_name, "r")
    lines = f.readlines()
    f.close()
    p_of_5_to_10 = lines[21:30]

    p_at = ['5', '10', '15', '20', '30', '100', '200', '500', '1000']
    precision_at = []
    for j in p_of_5_to_10:
        precision_at.append(float(j.split()[-1]))

    plt.plot(precision_at, label=file_name)
    plt.title('Precision Document Level Averages')
    plt.xlabel('At # of Documents')
    plt.ylabel('Precision')
    plt.xticks([0, 1, 2, 3, 4, 5, 6, 7, 8], p_at)

def main(argv):
    input_arg = ''
    output_arg = ''
    try:
        opts, args = getopt.getopt(argv, "hi:o:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print('plot.py -i <inputs_file> -o <output_file>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('plot.py -i <input_files> -o <output_file>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            input_arg = arg
        elif opt in ("-o", "--ofile"):
            output_arg = arg

    output_files = output_arg.split(",")

    input_files = input_arg.split(",")
    input_files.remove("")

    plt.clf()
    for input_file in input_files:
        make_RPC_picture(input_file)
    plt.legend(loc='upper right')
    plt.savefig(output_files[0])

    plt.clf()
    for input_file in input_files:
        make_p_at_picture(input_file)
    plt.legend(loc='upper right')
    plt.savefig(output_files[1])

if __name__ == "__main__":
    main(sys.argv[1:])