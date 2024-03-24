import sys
import pymorphy2
sys.stdout.reconfigure(encoding='utf-8')
def to_nominative(text):
    morph = pymorphy2.MorphAnalyzer()
    words = text.split()  # Split the text into words
    nominative_words = []

    for word in words:
        parsed_word = morph.parse(word)[0]  # Parse each word
        nominative_form = parsed_word.inflect({'nomn'})  # Attempt to convert to nominative case

        if nominative_form:
            nominative_words.append(nominative_form.word)
        else:
            nominative_words.append(word)  # Use the original word if no conversion is found

    return ' '.join(nominative_words)

if __name__ == "__main__":
    input_text = sys.argv[1]  # Read the input text from command line arguments
    result = to_nominative(input_text)
    print(result)
