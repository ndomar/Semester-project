require 'rubygems'
require 'Text'

class GrammarGenerator

	def read_sentences_from_file file_name
        sentences = Array.new
		counter = 1
		begin
    		file = File.new(file_name, "r")
   			while (line = file.gets)
        		counter = counter + 1
                sentences << line
    		end
    		file.close
		rescue => err
    		puts "Exception: #{err}"
    		err
		end
        sentences
	end

    def cluster_sentences threshold, sentences
        cluster = Hash.new
        covered_sentences = Array.new
        id = 0
        i = 0
        sentences.each do |s|
            if !covered_sentences.include? id
                j = 0
                temp = Array.new
                temp << s
                covered_sentences << id
                sentences.each do |sentence|
                    levenshtein_distance = get_levenshtein_distance s, sentence
                    if levenshtein_distance < threshold
                        temp << sentence if !temp.include? sentence
                        covered_sentences << j  
                    end
                    cluster[i] = temp
                    j += 1
                end
                i += 1
            end
            id = id+1
        end
        cluster
    end

    def get_levenshtein_distance t1, t2
        Text::Levenshtein.distance t1, t2
    end

    def generate_single_rule sentences
        splitted_sentences = Hash.new
        id = 0
        max = 0
        max_id = 0
        sentences.each do |s|
            splitted_sentences[id] = s.split " "
            if splitted_sentences[id].length > max 
                max = splitted_sentences[id].length
                max_id = id
            end
            id += 1
        end
        rule = "("
        i = 0
        while i < max do     
            temp = Array.new
            id = 0
            while id < splitted_sentences.length do 
                temp << splitted_sentences[id][i] if !temp.include? splitted_sentences[id][i] && i < splitted_sentences[id].length
                id += 1
            end
            rule += " ("
            temp.uniq.each do |atom|
                if atom != nil
                    rule += atom
                else
                    rule += "<>"
                end
                rule += " | "
            end
            rule = rule.chomp(' | ')
            rule += ") "
            i += 1
        end
        rule += ") " 
        rule
    end

    def generate_all_rules cluster
        i = 0
        rules = Array.new
        rule = generate_single_rule cluster[i]
        while i < cluster.length do
            rule = generate_single_rule cluster[i]
            rules << rule
            i += 1
        end
        rules
    end

    def generate_grammar_file file_in, file_out, threshold
        sentences = read_sentences_from_file file_in
        cluster = cluster_sentences threshold, sentences
        rules = generate_all_rules cluster
        out = "public <gr> = "
        counter = 0
        rules.each do |rule|
            out += "<" + "rule" + counter.to_s + "> "
            counter += 1
        end
        out += ";"
        counter = 0
        rules.each do |rule|
            out += "\n"
            out += "<" + "rule" + counter.to_s + "> = " + rule + ";"
            counter += 1
        end
        File.write file_out, out
        out
    end
end
object = GrammarGenerator.new
object.generate_grammar_file "text.txt", "out.txt", 5
