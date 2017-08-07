
use 5.010;
use strict;
use warnings;
use utf8;

my @saveWords;

my $count = 0;
open my $infile, '<', "google-books-common-words_filtered-With-Frequency.txt";
while (<$infile>){	
	my $line = $_;
	chomp($line);



	my @wholeLine = split ' ', $line;
	my @String = split '', $wholeLine[0];
	@String = sort @String;

	my $wordAlph = join '', @String;
	push @saveWords, "$wholeLine[0] $wordAlph $wholeLine[1]" ;

	$count++;
}


close $infile;
open my $outfile, '>', "google-books-common-words_filtered-with-Frequency_AlphabetisedLetters.txt";
foreach my $word (@saveWords){
	print $outfile "$word\n";
}
close $outfile;
